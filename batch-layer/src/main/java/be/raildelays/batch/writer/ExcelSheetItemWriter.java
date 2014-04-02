package be.raildelays.batch.writer;

import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;
import org.springframework.beans.factory.InitializingBean;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExcelSheetItemWriter extends AbstractItemStreamItemWriter<List<ExcelRow>> implements InitializingBean {

    private String input;

    private String output;

    private OutputStream outputStream;

    private OPCPackage inputStream;

    private XSSFWorkbook workbook;

    private static final int FIRST_ROW_INDEX = 21;

    private static final int MAX_ROW_PER_SHEET = 40;

    private int currentItemCount = 0;

    private boolean recoveryMode = false;

    private static final String EXCEL_FILE_EXTENSION = ".xlsx";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemWriter.class);

    private class Container {

        private XSSFWorkbook workbook;

        public Container(OPCPackage inputStream) {
            try {
                this.workbook = new XSSFWorkbook(inputStream);
            } catch (IOException e) {
                LOGGER.error("Error when opening an Excel workbook", e);
                throw new IllegalStateException("Cannot initialize the Container", e);
            }
        }

        public boolean contains(ExcelRow excelRow) {
            boolean result = false;
            Sheet sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();

            for (int i = FIRST_ROW_INDEX; i <= lastRowNum && i <= FIRST_ROW_INDEX + MAX_ROW_PER_SHEET; i++) {
                Row row = sheet.getRow(i);

                if (compare(row, excelRow) == 0) {
                    result = true;
                    setCurrentRowIndex(i);
                    break;
                }
            }

            return result;
        }

        public XSSFWorkbook getWorkbook() {
            return workbook;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(input,
                "You must provide an input before using this bean");
        Validate.notNull(output,
                "You must provide an output before using this bean");
        recoveryMode = false;
    }

    @Override
    public void write(List<? extends List<ExcelRow>> items) {
        if (workbook == null) {
            //-- We initialize the workbook reference based on the first processed item
            if (!items.isEmpty() && !items.get(0).isEmpty()) {
                ExcelRow firstItem = items.get(0).get(0);

                if (!checkExistingWorkbooks(firstItem)) {
                    createNewExcelFile(firstItem);
                }
            }
        }

        Sheet sheet = getFirstSheet();

        for (List<ExcelRow> item : items) {
            for (ExcelRow excelRow : item) {
                if (currentItemCount == MAX_ROW_PER_SHEET) {
                    closeCurrentExcelFile();
                    createNewExcelFile(excelRow);
                    sheet = getFirstSheet();
                }

                writeRow(sheet.getRow(getCurrentRowIndex()), excelRow);
            }
        }
    }

    @Override
    public void close() throws ItemStreamException {
        closeCurrentExcelFile();
    }

    private void writeRow(Row row, ExcelRow excelRow) {
        SimpleDateFormat hh = new SimpleDateFormat("HH");
        SimpleDateFormat mm = new SimpleDateFormat("mm");
        String departureStation = getStationName(excelRow.getDepartureStation());
        String arrivalStation = getStationName(excelRow.getArrivalStation());

        row.getCell(2).setCellValue(excelRow.getDate());
        row.getCell(12).setCellValue(departureStation);
        row.getCell(18).setCellValue(arrivalStation);
        if (excelRow.getLinkStation() != null) {
            row.getCell(24).setCellValue(
                    excelRow.getLinkStation().getEnglishName());
        }
        row.getCell(30).setCellValue(
                hh.format(excelRow.getExpectedDepartureTime()));
        row.getCell(32).setCellValue(
                mm.format(excelRow.getExpectedDepartureTime()));
        row.getCell(33).setCellValue(
                hh.format(excelRow.getExpectedArrivalTime()));
        row.getCell(35).setCellValue(
                mm.format(excelRow.getExpectedArrivalTime()));
        row.getCell(36).setCellValue(
                excelRow.getExpectedTrain1().getEnglishName());
        if (excelRow.getExpectedTrain2() != null) {
            row.getCell(39).setCellValue(
                    excelRow.getExpectedTrain2().getEnglishName());
        }
        row.getCell(42).setCellValue(
                hh.format(excelRow.getEffectiveDepartureTime()));
        row.getCell(44).setCellValue(
                mm.format(excelRow.getEffectiveDepartureTime()));
        row.getCell(45).setCellValue(
                hh.format(excelRow.getEffectiveArrivalTime()));
        row.getCell(47).setCellValue(
                mm.format(excelRow.getEffectiveArrivalTime()));
        row.getCell(48).setCellValue(
                excelRow.getEffectiveTrain1().getEnglishName());
        if (excelRow.getExpectedTrain2() != null) {
            row.getCell(51).setCellValue(
                    excelRow.getEffectiveTrain2().getEnglishName());
        }

        XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(workbook);
        evaluator.evaluateFormulaCell(row.getCell(56));
        evaluator.evaluateFormulaCell(row.getCell(55));
        evaluator.evaluateFormulaCell(row.getCell(54));
        evaluator.evaluateFormulaCell(row.getCell(53));
    }

    private void createNewExcelFile(ExcelRow firstItem) throws ItemStreamException {
        try {
            String fileName = "retard_sncb " + DateFormatUtils.format(firstItem.getDate(), "yyyyMMdd") + EXCEL_FILE_EXTENSION;
            File outputFile = new File(output + File.separator + fileName);

            currentItemCount = 0;

            if (outputFile.exists()) {
                boolean deleted = outputFile.delete();

                LOGGER.debug("Output file '{}' deleted:{}", outputFile.getAbsolutePath(), deleted);
            }

            boolean created = outputFile.createNewFile();

            LOGGER.debug("Output file '{}' created:{}", outputFile.getAbsolutePath(), created);

            inputStream = OPCPackage.open(new File(input));
            workbook = new XSSFWorkbook(inputStream);

            outputStream = new FileOutputStream(outputFile);
        } catch (IOException | InvalidFormatException e) {
            throw new ItemStreamException("I/O when opening Excel template", e);
        }
    }

    private boolean checkExistingWorkbooks(ExcelRow firstItem) {
        Validate.notNull(firstItem, "You must provide the first ExcelRow of this Excel sheet prior to check " +
                "if a file already exists.");

        // By comparing on new Container(null) value we are retrieving the first workbook containing the first free row.
        recoveryMode = retrieveFirstRowContaining(firstItem) || retrieveFirstRowContaining(null);

        return recoveryMode;
    }

    private boolean retrieveFirstRowContaining(ExcelRow content) {
        File directory = new File(output);
        this.workbook = null;

        Validate.isTrue(directory.isDirectory(), "The output '" + output + "' parameter must be a directory path and nothing else.");

        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(EXCEL_FILE_EXTENSION);
            }
        })) {
            try {
                OPCPackage inputStream = OPCPackage.open(file);

                try {
                    Container container = new Container(inputStream);

                    if (container.contains(content)) {
                        this.workbook = container.getWorkbook();
                        this.inputStream = inputStream;
                        break;
                    }
                } finally {
                    if (this.workbook == null) {
                        inputStream.revert(); //-- Must be done in order to free the file not used.
                    }
                }
            } catch (InvalidFormatException e) {
                LOGGER.error("Error when opening an Excel workbook '{}'", file.getAbsolutePath(), e);
            }
        }

        return this.workbook != null;
    }

    private void closeCurrentExcelFile() throws ItemStreamException {

        try {
            if (outputStream != null && workbook != null && inputStream != null) {
                if (recoveryMode) {
                    inputStream.close();
                } else {
                    workbook.write(outputStream);
                }
                workbook = null;
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when writing Excel output file", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error when closing Excel output file", e);
            }

            try {
                if (inputStream != null) {
                    if (!recoveryMode) {
                        inputStream.revert();
                    }
                }
            } catch (IllegalStateException e) {
                LOGGER.warn("Excel template stream already close!");
            }

        }


    }

    private static int compare(Row row, ExcelRow excelRow) {
        int result = 0;
        Cell dateCell = null;
        Cell stationFromCell = null;
        Cell stationToCell = null;

        if (row != null) {
            dateCell = row.getCell(2);
            stationFromCell = row.getCell(12);
            stationToCell = row.getCell(18);
        }

        if (dateCell != null && stationFromCell != null && stationToCell != null) {
            result = new CompareToBuilder()
                    .append(dateCell.getDateCellValue(), excelRow == null ? null : excelRow.getDate())
                    .append(stationFromCell.getStringCellValue(), excelRow == null ? null : getStationName(excelRow.getDepartureStation()))
                    .append(stationToCell.getStringCellValue(), excelRow == null ? null : getStationName(excelRow.getArrivalStation()))
                    .toComparison();
        } else if (excelRow != null) {
            result = 1;
        }

        return result;
    }

    private static String getStationName(Station station) {
        String result = "";

        if (station != null) {
            String stationName = station.getEnglishName();

            if (StringUtils.isNotBlank(stationName)) {
                result = StringUtils.stripAccents(stationName.toUpperCase(Locale.UK));
            }
        }

        return result;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private int getCurrentRowIndex() {
        return FIRST_ROW_INDEX + currentItemCount++;
    }

    private void setCurrentRowIndex(int rowIndex) {
        currentItemCount = rowIndex - FIRST_ROW_INDEX;
    }

    private Sheet getFirstSheet() {
        return workbook != null ? workbook.getSheetAt(0) : null;
    }


}
