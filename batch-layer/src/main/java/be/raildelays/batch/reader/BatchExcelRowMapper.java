package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.RowMapper;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Almex
 */
public class BatchExcelRowMapper implements RowMapper<BatchExcelRow> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchExcelRowMapper.class);

    private interface CellParser<T> {
        T getValue(Cell cell);
    }

    @Override
    public BatchExcelRow mapRow(Row row, int rowIndex) throws Exception {
        BatchExcelRow result = null;


        if (row.getCell(2) != null && row.getCell(2).getDateCellValue() != null) {

            result = new BatchExcelRow.Builder(getDate(row, 2), null)
                    .departureStation(getStation(row, 12))
                    .arrivalStation(getStation(row, 18))
                    .linkStation(getStation(row, 25))
                    .expectedDepartureTime(getHHMM(row, 30, 32))
                    .expectedArrivalTime(getHHMM(row, 33, 35))
                    .expectedTrain1(getTrain(row, 36))
                    .expectedTrain2(getTrain(row, 39))
                    .effectiveDepartureTime(getHHMM(row, 42, 44))
                    .effectiveArrivalTime(getHHMM(row, 45, 47))
                    .effectiveTrain1(getTrain(row, 48))
                    .effectiveTrain2(getTrain(row, 51))
                    .delay(getLong(row, 54))
                    .index((long) row.getRowNum())
                    .build();
        } //-- If the first cell contains nothing we return null

        return result;
    }

    private Train getTrain(Row row, int cellIndex) {
        NumberFormat numberFormat = new DecimalFormat("#");
        Train result = null;

        Long trainId = getLong(row, cellIndex);
        if (trainId != null) {
            result = new Train(numberFormat.format(trainId));
        }

        return result;
    }

    private Station getStation(Row row, int cellIndex) {
        Station result = null;

        String stationName = getString(row, cellIndex);
        if (StringUtils.isNotBlank(stationName)) {
            result = new Station(stationName);
        }

        return result;
    }

    private static <T> T getValue(Row row, int cellIndex, CellParser<T> parser) {
        T result = null;

        Cell cell = row.getCell(cellIndex);

        if (cell != null) {
            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                result = parser.getValue(cell);
            } // Otherwise we must return null
        } else {
            LOGGER.warn("Cannot map rowIndex={} cellIndex={} this cell does not exists", row.getRowNum(), cellIndex);
        }


        return result;
    }

    private Date getDate(Row row, final int cellIndex) {
        return getValue(row, cellIndex, new CellParser<Date>() {
            @Override
            public Date getValue(Cell cell) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date result = null;

                try {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            result = cell.getDateCellValue();
                            break;
                        case Cell.CELL_TYPE_STRING:
                            result = formatter.parse(cell.getStringCellValue());
                            break;
                        default:
                            LOGGER.error("Cannot convert rowIndex={} cellIndex={} of type={} into Date", cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType());
                    }
                } catch (ParseException e) {
                    LOGGER.error("Parsing exception: cannot convert into date rowIndex={} cellIndex={}, exception={}", cell.getRowIndex(), cellIndex, e.getMessage());
                }

                return result;
            }
        });
    }

    private Date getHHMM(Row row, int hhCellIndex, int mmCellIndex) {
        Date result = null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        NumberFormat numberFormat = new DecimalFormat("##");

        int hours = getTime(row, hhCellIndex);
        int minutes = getTime(row, mmCellIndex);

        try {
            if (hours >= 0 && minutes >= 0) {
                String hh = numberFormat.format(hours);
                String mm = numberFormat.format(minutes);

                result = formatter.parse(hh + ":" + mm);
            }
        } catch (ParseException e) {
            LOGGER.error("Parsing exception: cannot convert into date rowIndex={} hhCellIndex={} mmCellIndex={}, exception={}", row.getRowNum(), hhCellIndex, mmCellIndex, e.getMessage());
        }

        return result;
    }

    private Integer getTime(Row row, int cellIndex) {
        return getValue(row, cellIndex, new CellParser<Integer>() {
            @Override
            public Integer getValue(Cell cell) {
                Integer result = -1;
                NumberFormat numberFormat = new DecimalFormat("#");

                try {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            result = numberFormat.parse(cell.getStringCellValue()).intValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            result = (int) cell.getNumericCellValue();
                            break;
                        default:
                            LOGGER.error("Cannot convert rowIndex={} cellIndex={} of type={} into String", cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType());
                    }
                } catch (ParseException e) {
                    LOGGER.error("Parsing exception: cannot handle rowIndex={} cellIndex={} exception={}", cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
                }

                return result;
            }
        });
    }

    private String getString(Row row, int cellIndex) {
        return getValue(row, cellIndex, new CellParser<String>() {
            @Override
            public String getValue(Cell cell) {
                String result = null;

                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        result = cell.getStringCellValue();
                        break;
                    default:
                        LOGGER.error("Cannot convert rowIndex={} cellIndex={} of type={} into String", cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType());
                }

                return result;
            }
        });
    }

    private Long getLong(Row row, int cellIndex) {
        return getValue(row, cellIndex, new CellParser<Long>() {
            @Override
            public Long getValue(Cell cell) {
                NumberFormat numberFormat = new DecimalFormat("#");
                Long result = null;

                try {
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_NUMERIC:
                            result = (long) cell.getNumericCellValue();
                            break;
                        case Cell.CELL_TYPE_STRING:
                            result = numberFormat.parse(cell.getStringCellValue()).longValue();
                            break;
                        default:
                            LOGGER.error("Cannot convert rowIndex={} cellIndex={} of type={} into String", cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType());
                    }
                } catch (ParseException e) {
                    LOGGER.error("Parsing exception: cannot handle rowIndex={} cellIndex={} exception={}", cell.getRowIndex(), cell.getColumnIndex(), e.getMessage());
                }

                return result;
            }
        });
    }
}
