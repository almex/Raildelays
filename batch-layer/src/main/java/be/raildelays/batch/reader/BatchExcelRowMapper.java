package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.RowMapper;
import org.springframework.batch.item.file.RowMappingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Simple {@link org.springframework.batch.item.file.RowMapper} matching our use case to deal with our
 * {@link be.raildelays.batch.bean.BatchExcelRow}.
 *
 * @author Almex
 * @since 1.1
 */
public class BatchExcelRowMapper implements RowMapper<BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchExcelRowMapper.class);
    public static final int DATE_INDEX = 2;
    public static final int DEPARTURE_STATION_INDEX = 12;
    public static final int ARRIVAL_STATION_INDEX = 18;
    public static final int LINK_STATION_INDEX = 25;
    public static final int EXPECTED_DEPARTURE_HH_INDEX = 30;
    public static final int EXPECTED_DEPARTURE_MM_INDEX = 32;
    public static final int EFFECTIVE_TRAIN1_INDEX = 48;
    public static final int EFFECTIVE_TRAIN2_INDEX = 51;
    public static final int DELAY_INDEX = 54;
    public static final int EXPECTED_TRAIN1_INDEX = 36;
    public static final int EXPECTED_TRAIN2_INDEX = 39;
    public static final int EXPECTED_ARRIVAL_HH_INDEX = 33;
    public static final int EXPECTED_ARRIVAL_MM_INDEX = 35;
    public static final int EFFECTIVE_DEPARTURE_HH_INDEX = 42;
    public static final int EFFECTIVE_DEPARTURE_MM_INDEX = 44;
    public static final int EFFECTIVE_ARRIVAL_HH_INDEX = 45;
    public static final int EFFECTIVE_ARRIVAL_MM_INDEX = 47;

    private boolean validateOutcomes = false;

    private Validator validator;

    private String language = Language.EN.name();

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

    @Override
    public void afterPropertiesSet() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Assert.notNull(language, "You must set language before using this bean");
    }

    @Override
    public BatchExcelRow mapRow(Row row, int rowIndex) throws RowMappingException {
        BatchExcelRow result = null;

        if (!isEmpty(row)) {
            result = new BatchExcelRow.Builder(getDate(row, DATE_INDEX), null)
                    .departureStation(getStation(row, DEPARTURE_STATION_INDEX))
                    .arrivalStation(getStation(row, ARRIVAL_STATION_INDEX))
                    .linkStation(getStation(row, LINK_STATION_INDEX))
                    .expectedDepartureTime(getHHMM(row, EXPECTED_DEPARTURE_HH_INDEX, EXPECTED_DEPARTURE_MM_INDEX))
                    .expectedArrivalTime(getHHMM(row, EXPECTED_ARRIVAL_HH_INDEX, EXPECTED_ARRIVAL_MM_INDEX))
                    .expectedTrain1(getTrain(row, EXPECTED_TRAIN1_INDEX))
                    .expectedTrain2(getTrain(row, EXPECTED_TRAIN2_INDEX))
                    .effectiveDepartureTime(getHHMM(row, EFFECTIVE_DEPARTURE_HH_INDEX, EFFECTIVE_DEPARTURE_MM_INDEX))
                    .effectiveArrivalTime(getHHMM(row, EFFECTIVE_ARRIVAL_HH_INDEX, EFFECTIVE_ARRIVAL_MM_INDEX))
                    .effectiveTrain1(getTrain(row, EFFECTIVE_TRAIN1_INDEX))
                    .effectiveTrain2(getTrain(row, EFFECTIVE_TRAIN2_INDEX))
                    .delay(getLong(row, DELAY_INDEX))
                    .index((long) row.getRowNum())
                    .build();

            if (validateOutcomes) {
                validate(row, rowIndex, result);
            }
        } else {
            /*
             * If the first cell contains nothing we return an empty bean.
             * If we return null then it will be interpreted as EOF by any user of an ItemReader.
             */
            result = BatchExcelRow.EMPTY;
        }

        return result;
    }

    private void validate(Row row, int rowIndex, BatchExcelRow result) {
        Set<ConstraintViolation<BatchExcelRow>> constraintViolations = validator.validate(result);

        if (!constraintViolations.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for (ConstraintViolation<BatchExcelRow> constraintViolation : constraintViolations) {
                builder.append("\nConstraints violations occurred: ");
                builder.append(constraintViolation.getPropertyPath());
                builder.append(' ');
                builder.append(constraintViolation.getMessage());
            }

            throw new RowMappingException(builder.toString(), row, rowIndex);
        }
    }

    private Train getTrain(Row row, int cellIndex) {
        NumberFormat numberFormat = new DecimalFormat("#");
        Train result = null;

        Long trainId = getLong(row, cellIndex);
        if (trainId != null) {
            result = new Train(numberFormat.format(trainId), getLanguage());
        }

        return result;
    }

    private Station getStation(Row row, int cellIndex) {
        Station result = null;

        String stationName = getString(row, cellIndex);
        if (StringUtils.isNotBlank(stationName)) {
            result = new Station(stationName, getLanguage());
        }

        return result;
    }

    private boolean isEmpty(Row row) {
        boolean result = true;

        if (row != null) {
            result = isEmpty(row.getCell(DATE_INDEX))
                    && isEmpty(row.getCell(DEPARTURE_STATION_INDEX))
                    && isEmpty(row.getCell(ARRIVAL_STATION_INDEX))
                    && isEmpty(row.getCell(EXPECTED_TRAIN1_INDEX))
                    && isEmpty(row.getCell(EFFECTIVE_TRAIN1_INDEX));
        }

        return result;
    }

    private boolean isEmpty(Cell cell) {
        boolean result = true;

        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                case Cell.CELL_TYPE_NUMERIC:
                case Cell.CELL_TYPE_BOOLEAN:
                case Cell.CELL_TYPE_FORMULA:
                    result = false;
                    break;
            }
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

        Integer hours = getTime(row, hhCellIndex);
        Integer minutes = getTime(row, mmCellIndex);

        try {
            if (hours != null && minutes != null && hours >= 0 && minutes >= 0) {
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
                Integer result = null;
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
                        case Cell.CELL_TYPE_FORMULA:
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

    private Language getLanguage() {
        return Language.valueOf(language.toUpperCase());
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @SuppressWarnings("unused")
    public void setValidateOutcomes(boolean validateOutcomes) {
        this.validateOutcomes = validateOutcomes;
    }

    private interface CellParser<T> {
        T getValue(Cell cell);
    }
}
