/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.RowAggregator;
import org.springframework.batch.item.file.RowMappingException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Simple {@link org.springframework.batch.item.file.RowAggregator} matching our use case to deal with
 * {@link be.raildelays.batch.bean.BatchExcelRow}.
 *
 * @author Almex
 * @since 1.1
 */
public class ExcelRowAggregator implements RowAggregator<ExcelRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelRowAggregator.class);

    private String language = Language.EN.name();

    private BatchExcelRowMapper batchExcelRowMapper;


    public ExcelRowAggregator() {
        batchExcelRowMapper = new BatchExcelRowMapper();
        batchExcelRowMapper.setValidateOutcomes(true);
        batchExcelRowMapper.afterPropertiesSet();
    }

    private static <T> void setFormat(Row row, int cellIndex, T value, CellFormatter<T> formatter) {
        Cell cell = row.getCell(cellIndex);

        if (cell != null) {
            if (value != null) {
                formatter.setFormat(cell, value);
            } else {
                cell.setCellType(Cell.CELL_TYPE_BLANK);
            }
        } else {
            LOGGER.warn("Cannot aggregate rowIndex={} cellIndex={} this cell does not exists", row.getRowNum(), cellIndex);
        }
    }

    private static String getStationName(Station station, Language lang) {
        String result = "";

        if (station != null) {
            String stationName = station.getName(lang);

            if (StringUtils.isNotBlank(stationName)) {
                result = StringUtils.stripAccents(stationName.toUpperCase(Locale.UK));
            }
        }

        return result;
    }

    private static String getTrainName(TrainLine trainLine, Language lang) {
        String result = null;

        if (trainLine != null) {
            result = trainLine.getName(lang);
        }

        return result;
    }

    @Override
    public ExcelRow aggregate(ExcelRow item, Workbook workbook, int sheetIndex, int rowIndex) throws Exception {
        final Row row = workbook.getSheetAt(sheetIndex).getRow(rowIndex);
        BatchExcelRow previousRow = null;
        Language lang = Language.valueOf(language.toUpperCase());

        if (row != null && row.getCell(2) != null) {
            try {
                previousRow = batchExcelRowMapper.mapRow(row, rowIndex);
            } catch (RowMappingException e) {
                previousRow = null;
            }

            if (BatchExcelRow.EMPTY.equals(previousRow)) {
                previousRow = null;
            }

            setDateFormat(row, 2, item.getDate());
            setStringFormat(row, 12, getStationName(item.getDepartureStation(), lang));
            setStringFormat(row, 18, getStationName(item.getArrivalStation(), lang));
            setStringFormat(row, 24, getStationName(item.getLinkStation(), lang));
            setHHFormat(row, 30, item.getExpectedDepartureTime());
            setMMFormat(row, 32, item.getExpectedDepartureTime());
            setHHFormat(row, 33, item.getExpectedArrivalTime());
            setMMFormat(row, 35, item.getExpectedArrivalTime());
            setNumericFormat(row, 36, getTrainName(item.getExpectedTrainLine1(), lang));
            setNumericFormat(row, 39, getTrainName(item.getExpectedTrainLine2(), lang));
            setHHFormat(row, 42, item.getEffectiveDepartureTime());
            setMMFormat(row, 44, item.getEffectiveDepartureTime());
            setHHFormat(row, 45, item.getEffectiveArrivalTime());
            setMMFormat(row, 47, item.getEffectiveArrivalTime());
            setNumericFormat(row, 48, getTrainName(item.getEffectiveTrainLine1(), lang));
            setNumericFormat(row, 51, getTrainName(item.getEffectiveTrainLine2(), lang));
            evaluateFormula(workbook, row, 56);
            evaluateFormula(workbook, row, 55);
            evaluateFormula(workbook, row, 54);
        }

        return previousRow;
    }

    private void evaluateFormula(Workbook workbook, Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);

        try {
            final FormulaEvaluator evaluator = new WorkbookAction<FormulaEvaluator>(workbook) {

                @Override
                protected FormulaEvaluator doWithHSSFWorkbook(HSSFWorkbook workbook) {
                    return new HSSFFormulaEvaluator(workbook);
                }

                @Override
                protected FormulaEvaluator doWithXSSFWorkbook(XSSFWorkbook workbook) {
                    return new XSSFFormulaEvaluator(workbook);
                }
            }.execute();

            if (cell != null) {
                evaluator.evaluateFormulaCell(cell);
            } else {
                LOGGER.warn("Cannot aggregate rowIndex={} cellIndex={} this cell does not exists");
            }
        } catch (InvalidFormatException e) {
            LOGGER.error("Invalid format exception: cannot handle rowIndex={} cellIndex={} exception={}", row.getRowNum(), cellIndex, e.getMessage());
        }
    }

    private void setNumericFormat(Row row, int cellIndex, String number) {
        setFormat(row, cellIndex, number, new CellFormatter<String>() {
            NumberFormat numberFormat = new DecimalFormat("#");

            @Override
            public void setFormat(Cell cell, String number) {
                try {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            cell.setCellValue(number);
                            break;
                        case Cell.CELL_TYPE_BLANK:
                        case Cell.CELL_TYPE_NUMERIC:
                        default:
                            cell.setCellValue(numberFormat.parse(number).doubleValue());
                    }
                } catch (ParseException e) {
                    LOGGER.error("Parsing exception: cannot handle rowIndex={} cellIndex={} exception={}", cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
                }
            }
        });
    }

    private void setStringFormat(Row row, int cellIndex, String message) {
        setFormat(row, cellIndex, message, (cell, message1) -> {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                case Cell.CELL_TYPE_STRING:
                default:
                    cell.setCellValue(message1);
            }
        });
    }

    private void setDateFormat(Row row, int cellIndex, LocalDate date) {
        setFormat(row, cellIndex, date, (cell, date1) -> {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                case Cell.CELL_TYPE_NUMERIC:
                    cell.setCellValue(Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
                    break;
                case Cell.CELL_TYPE_STRING:
                default:
                    cell.setCellValue(date.format(DateTimeFormatter.ISO_DATE));
            }
        });
    }

    private void setTimeFormat(Row row, int cellIndex, LocalTime time, final String format) {
        setFormat(row, cellIndex, time, new CellFormatter<LocalTime>() {
            DateTimeFormatter hh = DateTimeFormatter.ofPattern(format);
            NumberFormat numberFormat = new DecimalFormat("##");

            @Override
            public void setFormat(Cell cell, LocalTime time) {
                try {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            cell.setCellValue(numberFormat.parse(hh.format(time)).doubleValue());
                            break;
                        case Cell.CELL_TYPE_BLANK:
                        case Cell.CELL_TYPE_STRING:
                        default:
                            cell.setCellValue(hh.format(time));
                    }
                } catch (ParseException e) {
                    LOGGER.error("Parsing exception: cannot handle rowIndex={} cellIndex={} exception={}", cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
                }
            }
        });
    }

    private void setHHFormat(Row row, int cellIndex, LocalTime time) {
        setTimeFormat(row, cellIndex, time, "HH");
    }

    private void setMMFormat(Row row, int cellIndex, LocalTime time) {
        setTimeFormat(row, cellIndex, time, "mm");
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private interface CellFormatter<T> {
        void setFormat(Cell cell, T value);
    }

    /**
     * In order to deal with the two format of an Excel File (e.g: OLE2 and OXML),
     * this class allow to define what do to when you have a {@link HSSFWorkbook}
     * and what to do when you have a {@link XSSFWorkbook}.
     *
     * @author Almex
     * @since 1.1
     */
    public abstract static class WorkbookAction<T> {
        protected Workbook internalWorkbook;

        public WorkbookAction(Workbook workbook) {
            this.internalWorkbook = workbook;
        }

        protected abstract T doWithHSSFWorkbook(HSSFWorkbook workbook);

        protected abstract T doWithXSSFWorkbook(XSSFWorkbook workbook);

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
}
