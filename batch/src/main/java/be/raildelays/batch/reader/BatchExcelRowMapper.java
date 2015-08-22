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

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Simple {@link org.springframework.batch.item.file.RowMapper} matching our use case to deal with our
 * {@link be.raildelays.batch.bean.BatchExcelRow}.
 *
 * @author Almex
 * @since 1.1
 */
public class BatchExcelRowMapper implements RowMapper<BatchExcelRow>, InitializingBean {

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
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchExcelRowMapper.class);
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
        try {
            BatchExcelRow result = new BatchExcelRow.Builder(getDate(row, DATE_INDEX), null)
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
                    .build(validateOutcomes);

            return result;
        } catch (ValidationException e) {
            throw new RowMappingException(e.getMessage(), row, rowIndex);
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

    private LocalDate getDate(Row row, final int cellIndex) {
        return getValue(row, cellIndex, cell -> {
            LocalDate result = null;

            try {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        result = LocalDateTime.ofInstant(cell.getDateCellValue().toInstant(), ZoneId.systemDefault()).toLocalDate();
                        break;
                    case Cell.CELL_TYPE_STRING:
                        result = LocalDate.parse(cell.getStringCellValue(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        break;
                    default:
                        LOGGER.error("Cannot convert rowIndex={} cellIndex={} of type={} into Date", cell.getRowIndex(), cell.getColumnIndex(), cell.getCellType());
                }
            } catch (DateTimeParseException e) {
                LOGGER.error("Parsing exception: cannot convert into date rowIndex={} cellIndex={}, exception={}", cell.getRowIndex(), cellIndex, e.getMessage());
            }

            return result;
        });
    }

    private LocalTime getHHMM(Row row, int hhCellIndex, int mmCellIndex) {
        LocalTime result = null;
        NumberFormat numberFormat = new DecimalFormat("##");
        Integer hours = getTime(row, hhCellIndex);
        Integer minutes = getTime(row, mmCellIndex);

        if (hours != null && minutes != null && hours >= 0 && minutes >= 0) {
            result = LocalTime.parse(String.format("%02d:%02d", hours, minutes));
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
