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

package be.raildelays.logging;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.Delays;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.Train;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class typed logger is responsible to strictly format our output log ot match column width.
 * <br/>
 * Expected result:
 * <pre>
 *    |=====|===================|======|==========|====|====|========|========|=====|=====|=====|=====|==|======|======|
 *    |     |                   |000638|          |0466|    |Liège-Gu|        |null |     |null |     |00|000639|000637|
 *    |=====|===================|======|==========|====|====|========|========|=====|=====|=====|=====|==|======|======|
 *    [Agg]: lacks_expected_time                   0466      Liège-Gu          null        null
 *    [Agg]: candidate                             0466      Liège-Gu          16:24       16:24
 *    [Agg]: lacks_expected_time                   0466      Brussels          null        null
 *    [Agg]: candidate                             0466      Brussels          16:24       16:24
 *    [Agg]: lacks_expected_time                   0466      Brussels          null        null
 *    [Agg]: candidate                             0466      Brussels          16:24       16:24
 *    [Agg]: after_processing    000638 11/07/2014 0466      Liège-Gu          16:24       16:24          000639 000637
 *    [Xls]: extracted_left      000636 11/07/2014 0466      Brussels          16:24       16:24          000637 000635
 *    [Xls]: extracted_left      000636 11/07/2014 0466      Brussels          16:24       16:24          000637 000635
 *    [Xls]: extracted_rigth     null
 *    [Xls]: extracted_rigth     null
 *    [Xls]: departure           000636 11/07/2014 0466      Brussels          16:24       16:24          000637 000635
 *    [Xls]: arrival             000636 11/07/2014 0466      Brussels          16:24       16:24          000637 000635
 *    [Nxt]: item                       11/07/2014 0466 0466 Brussels Liège-Gu 16:24 16:24 16:24 16:24 00
 *    [Nxt]: candidates[0]       000084 11/07/2014 0514      Liège-Gu          17:00       17:05          000685 000683
 *    [Nxt]: candidates[1]       000569 11/07/2014 0515      Liège-Gu          18:00       18:05          000570 000568
 *    [Nxt]: found_faster               11/07/2014 0466 0514 Brussels Liège-Gu 16:24 16:01 16:24 17:00 36
 *    [Ftr]: stop_result         null
 *    |=====|===================|======|==========|====|====|========|========|=====|=====|=====|=====|==|======|======|
 * </pre>
 *
 * @author Almex
 */
public class RaildelaysLogger implements Logger {

    private static final int STATION_LENGTH = 12;
    private static final int MESSAGE_LENGTH = 20;
    private static final int PREFIX_LENGTH = 3;
    private static final String ID_FORMAT = "000000";
    private static final String TRAIN_FORMAT = "0000";
    private static final String DELAY_FORMAT = "00";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    private static final int TOTAL_LENGTH = ID_FORMAT.length() + DATE_FORMAT.length() + 2 * TRAIN_FORMAT.length() +
            2 * STATION_LENGTH + 4 * TIME_FORMAT.length() + 2 * ID_FORMAT.length() + 12;
    private org.slf4j.Logger delegate;

    private Marker marker;

    private char separator = ' ';

    private String type;
    private Delegator<Direction> directionDelegator = new Delegator<Direction>() {
        @Override
        public String logLine(String message, Direction object) {
            return new LogLineBuilder()
                    .message(object.getLibelle())
                    .expectedTrain(getTrainId(object.getTrain()))
                    .departureStation(object.getFrom() != null ? object.getFrom().getName() : null)
                    .arrivalStation(object.getTo() != null ? object.getTo().getName() : null)
                    .build();
        }
    };
    private Delegator<Step> stepDelegator = new Delegator<Step>() {
        @Override
        public String logLine(String message, Step object) {
            TimeDelay departureTime = TimeDelay.of(object.getDateTime().toLocalTime(), object.getDelay());

            return new LogLineBuilder()
                    .message(message)
                    .departureStation(object.getStation() != null ? object.getStation().getName() : null)
                    .expectedDepartureTime(object.getDateTime().toLocalTime())
                    .effectiveDepartureTime(departureTime != null ? departureTime.getEffectiveTime() : null)
                    .canceledDeparture(object.isCanceled())
                    .canceledArrival(object.isCanceled())
                    .build();
        }
    };
    private Delegator<LineStop> lineStopDelegator = new Delegator<LineStop>() {
        @Override
        public String logLine(String message, LineStop object) {
            /**
             * We revert here departure and arrival because what we want to show here it's a stop:
             * - we reach the stop at the arrival time <-> we start our route at departure time
             * - we leave the stop at the departure time <-> we stop our route at arrival time
             */
            return new LogLineBuilder()
                    .message(message)
                    .id(object.getId())
                    .date(object.getDate())
                    .expectedTrain(getTrainId(object.getTrain()))
                    .departureStation(getStationName(object.getStation()))
                    .expectedDepartureTime(object.getArrivalTime() != null ? object.getArrivalTime().getExpectedTime() : null)
                    .expectedArrivalTime(object.getDepartureTime() != null ? object.getDepartureTime().getExpectedTime() : null)
                    .effectiveDepartureTime(object.getArrivalTime() != null ? object.getArrivalTime().getEffectiveTime() : null)
                    .effectiveArrivalTime(object.getDepartureTime() != null ? object.getDepartureTime().getEffectiveTime() : null)
                    .canceledDeparture(object.isCanceledArrival())
                    .canceledArrival(object.isCanceledDeparture())
                    .idPrevious(object.getPrevious() != null ? object.getPrevious().getId() : null)
                    .idNext(object.getNext() != null ? object.getNext().getId() : null)
                    .build();
        }
    };
    private Delegator<ExcelRow> excelRowDelegator = new Delegator<ExcelRow>() {
        @Override
        public String logLine(String message, ExcelRow object) {
            return new LogLineBuilder()
                    .message(message)
                    .id(object.getId())
                    .date(object.getDate())
                    .expectedTrain(getTrainId(object.getExpectedTrain1()))
                    .effectiveTrain(getTrainId(object.getEffectiveTrain1()))
                    .departureStation(getStationName(object.getDepartureStation()))
                    .arrivalStation(getStationName(object.getArrivalStation()))
                    .expectedDepartureTime(object.getExpectedDepartureTime())
                    .expectedArrivalTime(object.getExpectedArrivalTime())
                    .effectiveDepartureTime(object.getEffectiveDepartureTime())
                    .effectiveArrivalTime(object.getEffectiveArrivalTime())
                    .build();
        }
    };
    private Delegator<BatchExcelRow> batchExcelRowDelegator = new Delegator<BatchExcelRow>() {
        @Override
        public String logLine(String message, BatchExcelRow object) {
            return new LogLineBuilder()
                    .message(message)
                    .id(object.getId())
                    .date(object.getDate())
                    .expectedTrain(getTrainId(object.getExpectedTrain1()))
                    .effectiveTrain(getTrainId(object.getEffectiveTrain1()))
                    .departureStation(getStationName(object.getDepartureStation()))
                    .arrivalStation(getStationName(object.getArrivalStation()))
                    .expectedDepartureTime(object.getExpectedDepartureTime())
                    .expectedArrivalTime(object.getExpectedArrivalTime())
                    .effectiveDepartureTime(object.getEffectiveDepartureTime())
                    .effectiveArrivalTime(object.getEffectiveArrivalTime())
                    .canceledArrival(object.isCanceled())
                    .canceledDeparture(object.isCanceled())
                    .build();
        }
    };
    private Delegator<RouteLogDTO> routeLogDTODelegator = new Delegator<RouteLogDTO>() {
        @Override
        public String logLine(String message, RouteLogDTO object) {
            return new LogLineBuilder()
                    .message(message)
                    .date(object.getDate())
                    .expectedTrain(object.getTrainId() != null ? Long.parseLong(object.getTrainId()) : null)
                    .build();
        }
    };
    private Delegator<ServedStopDTO> servedStopDTODelegator = new Delegator<ServedStopDTO>() {
        @Override
        public String logLine(String message, ServedStopDTO object) {
            /**
             * We revert here departure and arrival because what we want to show here it's a stop:
             * - we reach the stop at the arrival time <-> we start our route at departure time
             * - we leave the stop at the departure time <-> we stop our route at arrival time
             */
            TimeDelay arrivalTime = TimeDelay.of(object.getArrivalTime(), object.getArrivalDelay());
            TimeDelay departureTime = TimeDelay.of(object.getDepartureTime(), object.getDepartureDelay());

            return new LogLineBuilder()
                    .message(message)
                    .departureStation(object.getStationName())
                    .expectedDepartureTime(object.getArrivalTime())
                    .expectedArrivalTime(object.getDepartureTime())
                    .effectiveDepartureTime(arrivalTime != null ? arrivalTime.getEffectiveTime() : null)
                    .effectiveArrivalTime(departureTime != null ? departureTime.getEffectiveTime() : null)
                    .canceledDeparture(object.isCanceled())
                    .canceledArrival(object.isCanceled())
                    .build();
        }
    };

    public RaildelaysLogger(String type, org.slf4j.Logger delegate) {
        this.type = type;
        this.delegate = delegate;
    }

    public RaildelaysLogger(String type, org.slf4j.Logger delegate, Marker marker) {
        this.type = type;
        this.delegate = delegate;
        this.marker = marker;
    }

    private static Long getTrainId(be.raildelays.domain.entities.Train train) {
        Long result = null;

        if (train != null) {
            try {
                if (StringUtils.isNotBlank(train.getEnglishName())) {
                    result = Long.parseLong(train.getEnglishName());
                } else if (StringUtils.isNotBlank(train.getFrenchName())) {
                    result = Long.parseLong(train.getFrenchName());
                } else if (StringUtils.isNotBlank(train.getDutchName())) {
                    result = Long.parseLong(train.getDutchName());
                }
            } catch (NumberFormatException e) {
                result = 0L;
            }
        }

        return result;
    }

    private static String getStationName(be.raildelays.domain.entities.Station station) {
        String result = null;

        if (station != null) {
            if (StringUtils.isNotBlank(station.getEnglishName())) {
                result = station.getEnglishName();
            } else if (StringUtils.isNotBlank(station.getFrenchName())) {
                result = station.getFrenchName();
            } else if (StringUtils.isNotBlank(station.getDutchName())) {
                result = station.getDutchName();
            }
        }

        return result;
    }

    private static Long getTrainId(Train train) {
        Long result = null;

        if (train != null && train.getIdRailtime() != null) {
            try {
                result = Long.parseLong(train.getIdRailtime());
            } catch (NumberFormatException e) {
                result = 0L;
            }
        }

        return result;
    }

    @Override
    public void info(String message, LineStop lineStop) {
        lineStopDelegator.log(message, Level.INFO, lineStop);
    }

    @Override
    public void debug(String message, LineStop lineStop) {
        lineStopDelegator.log(message, Level.DEBUG, lineStop);
    }

    @Override
    public void trace(String message, LineStop lineStop) {
        lineStopDelegator.log(message, Level.TRACE, lineStop);
    }

    @Override
    public void info(String message, List<LineStop> lineStops) {
        lineStopDelegator.log(message, Level.INFO, lineStops);
    }

    @Override
    public void debug(String message, List<LineStop> lineStops) {
        lineStopDelegator.log(message, Level.DEBUG, lineStops);
    }

    @Override
    public void trace(String message, List<LineStop> lineStops) {
        lineStopDelegator.log(message, Level.TRACE, lineStops);
    }

    @Override
    public void info(String message, ExcelRow excelRow) {
        excelRowDelegator.log(message, Level.INFO, excelRow);
    }

    @Override
    public void debug(String message, ExcelRow excelRow) {
        excelRowDelegator.log(message, Level.DEBUG, excelRow);
    }

    @Override
    public void trace(String message, ExcelRow excelRow) {
        excelRowDelegator.log(message, Level.TRACE, excelRow);
    }

    @Override
    public void info(String message, BatchExcelRow excelRow) {
        excelRowDelegator.log(message, Level.INFO, excelRow);
    }

    @Override
    public void debug(String message, BatchExcelRow excelRow) {
        excelRowDelegator.log(message, Level.DEBUG, excelRow);
    }

    @Override
    public void trace(String message, BatchExcelRow excelRow) {
        excelRowDelegator.log(message, Level.TRACE, excelRow);
    }

    @Override
    public void info(String message, RouteLogDTO routeLog) {
        if (routeLog != null) {
            routeLogDTODelegator.log(message, Level.INFO, routeLog);
            servedStopDTODelegator.log("stops", Level.INFO, routeLog.getStops());
        }
    }

    @Override
    public void debug(String message, RouteLogDTO routeLog) {
        if (routeLog != null) {
            routeLogDTODelegator.log(message, Level.DEBUG, routeLog);
            servedStopDTODelegator.log("stops", Level.DEBUG, routeLog.getStops());
        }
    }

    @Override
    public void trace(String message, RouteLogDTO routeLog) {
        if (routeLog != null) {
            routeLogDTODelegator.log(message, Level.TRACE, routeLog);
            servedStopDTODelegator.log("stops", Level.TRACE, routeLog.getStops());
        }
    }

    @Override
    public void info(String message, TwoDirections twoDirections) {
        if (twoDirections != null) {
            if (twoDirections.getDeparture() != null) {
                directionDelegator.log(message, Level.INFO, twoDirections.getDeparture());
                stepDelegator.log(message, Level.INFO, twoDirections.getDeparture().getSteps());
            }
            if (twoDirections.getArrival() != null) {
                directionDelegator.log(message, Level.INFO, twoDirections.getArrival());
                stepDelegator.log(message, Level.INFO, twoDirections.getArrival().getSteps());
            }
        }
    }

    @Override
    public void debug(String message, TwoDirections twoDirections) {
        if (twoDirections != null) {
            if (twoDirections.getDeparture() != null) {
                directionDelegator.log(message, Level.DEBUG, twoDirections.getDeparture());
                stepDelegator.log(message, Level.DEBUG, twoDirections.getDeparture().getSteps());
            }
            if (twoDirections.getArrival() != null) {
                directionDelegator.log(message, Level.DEBUG, twoDirections.getArrival());
                stepDelegator.log(message, Level.DEBUG, twoDirections.getArrival().getSteps());
            }
        }
    }

    @Override
    public void trace(String message, TwoDirections twoDirections) {
        if (twoDirections != null) {
            if (twoDirections.getDeparture() != null) {
                directionDelegator.log(message, Level.TRACE, twoDirections.getDeparture());
                stepDelegator.log(message, Level.TRACE, twoDirections.getDeparture().getSteps());
            }
            if (twoDirections.getArrival() != null) {
                directionDelegator.log(message, Level.TRACE, twoDirections.getArrival());
                stepDelegator.log(message, Level.TRACE, twoDirections.getArrival().getSteps());
            }
        }
    }

    public void setDelegate(org.slf4j.Logger logger) {
        this.delegate = logger;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String msg) {

    }

    @Override
    public void trace(String format, Object arg) {

    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(String format, Object... arguments) {

    }

    @Override
    public void trace(String msg, Throwable t) {

    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return false;
    }

    @Override
    public void trace(Marker marker, String msg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg) {

    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {

    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {

    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {

    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
        delegate.debug(msg);
    }

    @Override
    public void debug(String format, Object arg) {
        delegate.debug(format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        delegate.debug(format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        delegate.debug(format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        delegate.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        delegate.debug(marker, msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        delegate.debug(marker, format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        delegate.debug(marker, format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        delegate.debug(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(String format, Object arg) {
        delegate.info(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        delegate.info(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        delegate.info(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        delegate.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        delegate.info(marker, msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        delegate.info(marker, format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        delegate.info(marker, format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        delegate.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        delegate.warn(msg);
    }

    @Override
    public void warn(String format, Object arg) {
        delegate.warn(format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        delegate.warn(format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        delegate.warn(format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        delegate.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        delegate.warn(marker, msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        delegate.warn(marker, format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        delegate.warn(marker, format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        delegate.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(String format, Object arg) {
        delegate.error(format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        delegate.error(format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        delegate.error(format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        delegate.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        delegate.error(marker, msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        delegate.error(marker, format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        delegate.error(marker, format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        delegate.error(marker, msg, t);
    }

    private enum Level {DEBUG, TRACE, INFO}

    private class LogLineBuilder {

        private String message;
        private Long id;
        private LocalDate date;
        private Long expectedTrain;
        private Long effectiveTrain;
        private String departureStation;
        private String arrivalStation;
        private LocalTime expectedDepartureTime;
        private LocalTime expectedArrivalTime;
        private LocalTime effectiveDepartureTime;
        private LocalTime effectiveArrivalTime;
        private Long idPrevious;
        private Long idNext;
        private boolean canceledDeparture;
        private boolean canceledArrival;

        public LogLineBuilder message(String message) {
            this.message = message;

            return this;
        }

        public LogLineBuilder id(Long id) {
            this.id = id;

            return this;
        }

        public LogLineBuilder idPrevious(Long idPrevious) {
            this.idPrevious = idPrevious;

            return this;
        }

        public LogLineBuilder idNext(Long idNext) {
            this.idNext = idNext;

            return this;
        }

        public LogLineBuilder date(LocalDate date) {
            this.date = date;

            return this;
        }

        public LogLineBuilder expectedTrain(Long expectedTrain) {
            this.expectedTrain = expectedTrain;

            return this;
        }

        public LogLineBuilder effectiveTrain(Long effectiveTrain) {
            this.effectiveTrain = effectiveTrain;

            return this;
        }

        public LogLineBuilder arrivalStation(String arrivalStation) {
            this.arrivalStation = arrivalStation;

            return this;
        }

        public LogLineBuilder departureStation(String departureStation) {
            this.departureStation = departureStation;

            return this;
        }

        public LogLineBuilder expectedDepartureTime(LocalTime expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;

            return this;
        }

        public LogLineBuilder expectedArrivalTime(LocalTime expectedArrivalTime) {
            this.expectedArrivalTime = expectedArrivalTime;

            return this;
        }

        public LogLineBuilder effectiveDepartureTime(LocalTime effectiveDepartureTime) {
            this.effectiveDepartureTime = effectiveDepartureTime;

            return this;
        }

        public LogLineBuilder effectiveArrivalTime(LocalTime effectiveArrivalTime) {
            this.effectiveArrivalTime = effectiveArrivalTime;

            return this;
        }

        public LogLineBuilder canceledDeparture(boolean canceled) {
            this.canceledDeparture = canceled;

            return this;
        }

        public LogLineBuilder canceledArrival(boolean canceled) {
            this.canceledArrival = canceled;

            return this;
        }


        public String build() {
            final StringBuilder builder = new StringBuilder();
            final NumberFormat idFormat = new DecimalFormat(ID_FORMAT);
            final NumberFormat trainFormat = new DecimalFormat(TRAIN_FORMAT);
            final NumberFormat delayFormat = new DecimalFormat(DELAY_FORMAT);
            final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_FORMAT);
            final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);

            if (expectedTrain != null ||
                    departureStation != null ||
                    expectedDepartureTime != null ||
                    effectiveDepartureTime != null) {
                builder.append(StringUtils.rightPad(id != null ? idFormat.format(id) : "null", ID_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(date != null ? dateFormat.format(date) : "", DATE_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(expectedTrain != null ? trainFormat.format(expectedTrain) : "", TRAIN_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(effectiveTrain != null ? trainFormat.format(effectiveTrain) : "", TRAIN_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(departureStation != null ? substringCenter(departureStation, STATION_LENGTH, '~') : "", STATION_LENGTH));
                builder.append(separator);
                builder.append(StringUtils.rightPad(arrivalStation != null ? substringCenter(arrivalStation, STATION_LENGTH, '~') : "", STATION_LENGTH));
                builder.append(separator);
                builder.append(StringUtils.rightPad(expectedDepartureTime != null ? timeFormat.format(expectedDepartureTime) : "null", TIME_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(expectedArrivalTime != null ? timeFormat.format(expectedArrivalTime) : "null", TIME_FORMAT.length()));
                builder.append(separator);
                builder.append(formatEffectiveTime(effectiveDepartureTime, canceledDeparture));
                builder.append(separator);
                builder.append(formatEffectiveTime(effectiveArrivalTime, canceledArrival));
                builder.append(separator);
                builder.append(delayFormat.format(Delays.toMinutes(Delays.computeDelay(expectedArrivalTime, effectiveArrivalTime))));
                builder.append(separator);
                builder.append(StringUtils.rightPad(idPrevious != null ? idFormat.format(idPrevious) : "", ID_FORMAT.length()));
                builder.append(separator);
                builder.append(StringUtils.rightPad(idNext != null ? idFormat.format(idNext) : "", ID_FORMAT.length()));
            } else {
                builder.append(StringUtils.rightPad("null", TOTAL_LENGTH));
            }
            builder.append(separator);


            return builder.toString();
        }

        public String formatEffectiveTime(LocalTime effectiveTime, boolean canceled) {
            final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(TIME_FORMAT);
            final DateTimeFormatter canceledTimeFormat = DateTimeFormatter.ofPattern("HH'x'mm");
            final String result;

            if (effectiveTime != null) {
                result = StringUtils.rightPad(effectiveTime.format(canceled ? canceledTimeFormat : timeFormat), TIME_FORMAT.length());
            } else {
                result = StringUtils.center(canceled ? "x" : "", TIME_FORMAT.length());
            }

            return result;
        }

        public String substringCenter(String characters, int length, char centerCharacter) {
            String result = null;


            if (characters != null) {
                StringBuilder builder = new StringBuilder(length);

                if (characters.length() <= length) {
                    result = characters;
                } else {
                    builder.append(characters.substring(0, length - 4));
                    builder.append(centerCharacter);
                    builder.append(characters.substring(characters.length() - 3));
                    result = builder.toString();
                }
            }

            return result;
        }

    }

    private abstract class Delegator<T> {

        public abstract String logLine(String message, T object);

        public void log(String message, Level level, T object) {
            final StringBuilder builder = new StringBuilder();

            builder.append(separator);
            builder.append(StringUtils.rightPad(type != null ? "[" + StringUtils.substring(type, 0, PREFIX_LENGTH) + "]" : "", PREFIX_LENGTH + 2));
            builder.append(separator);
            builder.append(StringUtils.rightPad(message != null ? StringUtils.substring(message, 0, MESSAGE_LENGTH) : "", MESSAGE_LENGTH));
            builder.append(separator);

            if (object != null) {
                builder.append(logLine(message, object));
            } else {
                builder.append(StringUtils.rightPad("null", TOTAL_LENGTH));
            }

            switch (level) {
                case DEBUG:
                    delegate.debug(builder.toString());
                    break;
                case INFO:
                    delegate.info(builder.toString());
                    break;
                case TRACE:
                    delegate.trace(builder.toString());
                    break;
            }

        }

        public void log(String message, Level level, List<? extends T> objects) {
            if (objects != null) {
                for (int i = 0; i < objects.size(); i++) {
                    T object = objects.get(i);
                    log(message + "[" + i + "]", level, object);
                }
            }
        }
    }
}
