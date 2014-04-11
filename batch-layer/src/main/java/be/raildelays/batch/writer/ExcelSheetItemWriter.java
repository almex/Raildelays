package be.raildelays.batch.writer;

import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
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

    private String templatePath;

    private String outputDirectory;

    private OutputStream outputStream;

    private Closeable inputStream;

    protected Workbook workbook;

    private static final int FIRST_ROW_INDEX = 21;

    private static final int MAX_ROW_PER_SHEET = 40;

    private int currentItemCount = 0;

    private boolean recoveryMode = false;

    enum Format {
        OLE2(".xls"), OOXML(".xlsx");

        private String fileExtension;

        Format(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemWriter.class);

    protected class WorkbookSearch {

        private Workbook workbook;

        public WorkbookSearch(Workbook workbook) {
            this.workbook = workbook;
        }

        public int indexOf(ExcelRow excelRow) {
            int result = -1;
            Sheet sheet = workbook.getSheetAt(0);

            int lastRowNum = sheet.getLastRowNum();

            for (int i = FIRST_ROW_INDEX; i <= lastRowNum && i <= FIRST_ROW_INDEX + MAX_ROW_PER_SHEET; i++) {
                Row row = sheet.getRow(i);

                if (compare(row, excelRow) == 0) {
                    result = i;
                    break;
                }
            }

            return result;
        }
    }

    protected abstract class WorkbookAction<T> {
        protected abstract T doWithHSSFWorkbook(HSSFWorkbook workbook);

        protected abstract T doWithXSSFWorkbook(XSSFWorkbook workbook);

        protected Workbook internalWorkbook;

        public WorkbookAction() {
            this.internalWorkbook = workbook;
        }

        public WorkbookAction(Workbook workbook) {
            this.internalWorkbook = workbook;
        }

        public T execute() throws InvalidFormatException {
            if (internalWorkbook instanceof HSSFWorkbook) {
                return doWithHSSFWorkbook((HSSFWorkbook) internalWorkbook);
            } else if (internalWorkbook instanceof XSSFWorkbook) {
                return doWithXSSFWorkbook((XSSFWorkbook) internalWorkbook);
            } else {
                throw new InvalidFormatException("Format not supported!");
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(templatePath,
                "You must provide an templatePath before using this bean");
        Validate.notNull(outputDirectory,
                "You must provide an outputDirectory before using this bean");
        recoveryMode = false;
    }

    @Override
    public void write(List<? extends List<ExcelRow>> items) throws InvalidFormatException {
        try {
            if (workbook == null) {
                //-- We initialize the workbook reference based on the first processed item
                if (!items.isEmpty() && !items.get(0).isEmpty()) {
                    ExcelRow firstItem = items.get(0).get(0);

                    if (!isExistingWorkbooks(firstItem)) {
                        createNewWorkbook(getFileName(firstItem));
                    }
                }
            }

            Sheet sheet = getCurrentSheet();

            for (List<ExcelRow> item : items) {
                for (ExcelRow excelRow : item) {
                    if (currentItemCount == MAX_ROW_PER_SHEET) {
                        closeWorkbook();
                        createNewWorkbook(getFileName(excelRow));
                        sheet = getCurrentSheet();
                    }

                    writeRow(sheet.getRow(incrementRowIndex()), excelRow);
                }
            }
        } catch(IOException e) {
            LOGGER.error("We were not able to write in the Excel file", e);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        closeWorkbook();
    }

    private void writeRow(Row row, ExcelRow excelRow) throws InvalidFormatException {
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

        FormulaEvaluator evaluator = new WorkbookAction<FormulaEvaluator>() {

            @Override
            protected FormulaEvaluator doWithHSSFWorkbook(HSSFWorkbook workbook) {
                return new HSSFFormulaEvaluator(workbook);
            }

            @Override
            protected FormulaEvaluator doWithXSSFWorkbook(XSSFWorkbook workbook) {
                return new XSSFFormulaEvaluator(workbook);
            }
        }.execute();

        evaluator.evaluateFormulaCell(row.getCell(56));
        evaluator.evaluateFormulaCell(row.getCell(55));
        evaluator.evaluateFormulaCell(row.getCell(54));
        evaluator.evaluateFormulaCell(row.getCell(53));
    }

    private void createNewWorkbook(String fileName) throws ItemStreamException {
        try {
            final InputStream internalInputStream = new FileInputStream(templatePath);

            this.workbook = WorkbookFactory.create(internalInputStream);
            this.inputStream = extractCloseable(workbook, internalInputStream);

            File outputFile = new File(outputDirectory + File.separator + fileName);

            currentItemCount = 0;

            if (outputFile.exists()) {
                boolean deleted = outputFile.delete();

                LOGGER.debug("Output file '{}' deleted:{}", outputFile.getAbsolutePath(), deleted);
            }

            boolean created = outputFile.createNewFile();

            LOGGER.debug("Output file '{}' created:{}", outputFile.getAbsolutePath(), created);

            this.outputStream = new FileOutputStream(outputFile);
        } catch (IOException | InvalidFormatException e) {
            throw new ItemStreamException("I/O when opening Excel template", e);
        }
    }

    private boolean isExistingWorkbooks(ExcelRow firstItem) {
        Validate.notNull(firstItem, "You must provide the first ExcelRow of this Excel sheet prior to check " +
                "if a file already exists.");

        // By comparing on new WorkbookSearch(null) fileExtension we are retrieving the first workbook containing the first free row.
        recoveryMode = retrieveFirstRowContaining(firstItem) || retrieveFirstRowContaining(null);

        return recoveryMode;
    }

    private boolean retrieveFirstRowContaining(ExcelRow content) {
        File directory = new File(outputDirectory);
        this.workbook = null;

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + outputDirectory + "' parameter must be a directory path and nothing else.");

        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
            }
        })) {
            try {
                final InputStream currentInputStream = new FileInputStream(file);
                final Workbook currentWorkbook = WorkbookFactory.create(currentInputStream);

                try {
                    WorkbookSearch container = new WorkbookSearch(currentWorkbook);

                    int currentRowIndex = container.indexOf(content);
                    if (currentRowIndex != -1) {
                        this.workbook = currentWorkbook;
                        this.outputStream = new FileOutputStream(file);
                        this.inputStream = extractCloseable(currentWorkbook, currentInputStream);

                        setCurrentRowIndex(currentRowIndex);
                        break;
                    }
                } finally {
                    if (this.workbook == null) {
                        try {
                            extractCloseable(currentWorkbook, currentInputStream).close();
                        } catch (IOException e) {
                            LOGGER.warn("Error when closing Excel InputStream during consultation.", e);
                        }
                    }
                }
            } catch (InvalidFormatException e) {
                LOGGER.error("Excel format not supported for this workbook!", e);
            } catch (IOException e) {
                LOGGER.error("Error when opening an Excel workbook", e);
            }
        }

        return this.workbook != null;
    }

    private Closeable extractCloseable(final Workbook workbook, final Closeable closeable) throws InvalidFormatException {
        return new WorkbookAction<Closeable>(workbook) {

            @Override
            protected Closeable doWithHSSFWorkbook(HSSFWorkbook workbook) {
                return closeable;
            }

            @Override
            protected Closeable doWithXSSFWorkbook(XSSFWorkbook workbook) {
                return workbook.getPackage();
            }
        }.execute();
    }

    private void closeWorkbook() throws ItemStreamException {

        try {
            if (outputStream != null && workbook != null) {
                workbook.write(outputStream);
                workbook = null;
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when writing Excel outputDirectory file", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error when closing Excel outputDirectory file", e);
            }

            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error when closing Excel templatePath file", e);
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
                    .append(stationFromCell.getStringCellValue(), excelRow == null ? "" : getStationName(excelRow.getDepartureStation()))
                    .append(stationToCell.getStringCellValue(), excelRow == null ? "" : getStationName(excelRow.getArrivalStation()))
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

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    protected final int getCurrentItemCount() {
        return currentItemCount;
    }

    private final void setCurrentItemCount(int currentItemCount) {
        this.currentItemCount = currentItemCount;
    }

    protected final int incrementItemCount() {
        return currentItemCount++;
    }

    protected int getCurrentRowIndex() {
        return FIRST_ROW_INDEX + getCurrentItemCount();
    }

    protected int incrementRowIndex() {
        return FIRST_ROW_INDEX + incrementItemCount();
    }

    protected void setCurrentRowIndex(int rowIndex) {
        setCurrentItemCount(rowIndex - FIRST_ROW_INDEX);
    }

    protected Sheet getCurrentSheet() {
        return workbook != null ? workbook.getSheetAt(0) : null;
    }

    private String getFileName(ExcelRow firstItem) throws InvalidFormatException, IOException {
        String fileExtension = Format.OOXML.getFileExtension();
        InputStream inputStream = null;

        try {
            inputStream = new PushbackInputStream(new FileInputStream(templatePath), 8);

            if (POIFSFileSystem.hasPOIFSHeader(inputStream)) {
                fileExtension = Format.OLE2.getFileExtension();
            } else if (!POIXMLDocument.hasOOXMLHeader(inputStream)) {
                throw new InvalidFormatException("Your template is neither an OLE2 format, nor an OOXML format");
            }

            return "retard_sncb " + DateFormatUtils.format(firstItem.getDate(), "yyyyMMdd") + fileExtension;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("We were not able to determine the template format", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


}
