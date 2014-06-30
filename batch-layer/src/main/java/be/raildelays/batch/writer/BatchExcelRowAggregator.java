package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.RowAggregator;
import be.raildelays.batch.poi.WorkbookAction;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Almex
 */
public class BatchExcelRowAggregator implements RowAggregator<BatchExcelRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchExcelRowAggregator.class);


    private interface CellFormatter<T> {
        void setFormat(Cell cell, T value);
    }


    @Override
    public BatchExcelRow aggregate(BatchExcelRow item, Workbook workbook, int sheetIndex, int rowIndex) throws Exception {
        final Row row = workbook.getSheetAt(sheetIndex).getRow(rowIndex);
        BatchExcelRow previousRow = null;

        if (row != null && row.getCell(2) != null) {
            previousRow = new BatchExcelRowMapper().mapRow(row, rowIndex);

            setDateFormat(row, 2, item.getDate());
            setStringFormat(row, 12, getStationName(item.getDepartureStation()));
            setStringFormat(row, 18, getStationName(item.getArrivalStation()));
            setStringFormat(row, 24, getStationName(item.getLinkStation()));
            setHHFormat(row, 30, item.getExpectedDepartureTime());
            setMMFormat(row, 32, item.getExpectedDepartureTime());
            setHHFormat(row, 33, item.getExpectedArrivalTime());
            setMMFormat(row, 35, item.getExpectedArrivalTime());
            setNumericFormat(row, 36, getTrainName(item.getExpectedTrain1()));
            setNumericFormat(row, 39, getTrainName(item.getExpectedTrain2()));
            setHHFormat(row, 42, item.getEffectiveDepartureTime());
            setMMFormat(row, 44, item.getEffectiveDepartureTime());
            setHHFormat(row, 45, item.getEffectiveArrivalTime());
            setMMFormat(row, 47, item.getEffectiveArrivalTime());
            setNumericFormat(row, 48, getTrainName(item.getEffectiveTrain1()));
            setNumericFormat(row, 51, getTrainName(item.getEffectiveTrain2()));
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
        setFormat(row, cellIndex, message, new CellFormatter<String>() {

            @Override
            public void setFormat(Cell cell, String message) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BLANK:
                    case Cell.CELL_TYPE_STRING:
                    default:
                        cell.setCellValue(message);
                }
            }
        });
    }

    private void setDateFormat(Row row, int cellIndex, Date time) {
        setFormat(row, cellIndex, time, new CellFormatter<Date>() {

            @Override
            public void setFormat(Cell cell, Date date) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        cell.setCellValue(date.getTime());
                    case Cell.CELL_TYPE_BLANK:
                    case Cell.CELL_TYPE_STRING:
                    default:
                        cell.setCellValue(date);
                }
            }
        });
    }


    private void setTimeFormat(Row row, int cellIndex, Date time, final String format) {
        setFormat(row, cellIndex, time, new CellFormatter<Date>() {
            SimpleDateFormat hh = new SimpleDateFormat(format);
            NumberFormat numberFormat = new DecimalFormat("##");

            @Override
            public void setFormat(Cell cell, Date time) {
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


    private void setHHFormat(Row row, int cellIndex, Date time) {
        setTimeFormat(row, cellIndex, time, "HH");
    }

    private void setMMFormat(Row row, int cellIndex, Date time) {
        setTimeFormat(row, cellIndex, time, "mm");
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

    private static String getTrainName(Train train) {
        String result = null;

        if (train != null) {
            result = train.getEnglishName();
        }

        return result;
    }
}
