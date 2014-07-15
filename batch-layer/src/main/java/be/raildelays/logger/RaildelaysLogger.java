package be.raildelays.logger;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.Duration;
import org.joda.time.LocalTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private org.slf4j.Logger delegate;

    private char separator = ' ';

    private String type;

    public RaildelaysLogger(String type, org.slf4j.Logger delegate) {
        this.type = type;
        this.delegate = delegate;
    }

    private enum Level {DEBUG, TRACE, INFO}


    private class LogLineBuilder {

        private String message;
        private Long id;
        private Date date;
        private Long expectedTrain;
        private Long effectiveTrain;
        private String departureStation;
        private String arrivalStation;
        private Date expectedDepartureTime;
        private Date expectedArrivalTime;
        private Date effectiveDepartureTime;
        private Date effectiveArrivalTime;
        private Long idPrevious;
        private Long idNext;

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

        public LogLineBuilder date(Date date) {
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

        public LogLineBuilder expectedDepartureTime(Date expectedDepartureTime) {
            this.expectedDepartureTime = expectedDepartureTime;

            return this;
        }

        public LogLineBuilder expectedArrivalTime(Date expectedArrivalTime) {
            this.expectedArrivalTime = expectedArrivalTime;

            return this;
        }

        public LogLineBuilder effectiveDepartureTime(Date effectiveDepartureTime) {
            this.effectiveDepartureTime = effectiveDepartureTime;

            return this;
        }

        public LogLineBuilder effectiveArrivalTime(Date effectiveArrivalTime) {
            this.effectiveArrivalTime = effectiveArrivalTime;

            return this;
        }


        public String build() {
            final StringBuilder builder = new StringBuilder();
            final NumberFormat idFormat = new DecimalFormat("######");
            final NumberFormat trainFormat = new DecimalFormat("####");
            final NumberFormat delayFormat = new DecimalFormat("##");
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            builder.append(separator);
            builder.append(type != null ? "[" + type.substring(0, 3) + "]" : "     ");
            builder.append(separator);
            builder.append(message != null ? message.substring(0, 20) : "                    ");
            builder.append(separator);
            if (expectedTrain != null ||
                    departureStation != null ||
                    expectedDepartureTime != null ||
                    effectiveDepartureTime != null) {
                builder.append(id != null ? idFormat.format(id) : "null  ");
                builder.append(separator);
                builder.append(date != null ? dateFormat.format(date) : "      ");
                builder.append(separator);
                builder.append(expectedTrain != null ? trainFormat.format(expectedTrain) : "    ");
                builder.append(separator);
                builder.append(effectiveTrain != null ? trainFormat.format(effectiveTrain) : "    ");
                builder.append(separator);
                builder.append(departureStation != null ? departureStation.substring(0, 12) : "            ");
                builder.append(separator);
                builder.append(arrivalStation != null ? arrivalStation.substring(0, 12) : "            ");
                builder.append(separator);
                builder.append(expectedDepartureTime != null ? timeFormat.format(expectedDepartureTime) : "null ");
                builder.append(separator);
                builder.append(expectedArrivalTime != null ? timeFormat.format(expectedArrivalTime) : "null ");
                builder.append(separator);
                builder.append(effectiveDepartureTime != null ? timeFormat.format(effectiveDepartureTime) : "     ");
                builder.append(separator);
                builder.append(effectiveArrivalTime != null ? timeFormat.format(effectiveArrivalTime) : "     ");
                builder.append(separator);
                builder.append(delayFormat.format(computeDelay()));
                builder.append(separator);
                builder.append(idPrevious != null ? idFormat.format(idPrevious) : "null  ");
                builder.append(separator);
                builder.append(idNext != null ? idFormat.format(idNext) : "null  ");
                builder.append(separator);
            } else {
                builder.append("null                                                                                                ");
                builder.append(separator);
            }


            return builder.toString();
        }

        private Long computeDelay() {
            Long result = 0L;

            if (expectedArrivalTime != null && effectiveArrivalTime != null) {
                LocalTime expected = new LocalTime(expectedArrivalTime);
                LocalTime effective = new LocalTime(effectiveArrivalTime);
                Duration duration = new Duration(effective.toDateTimeToday(), expected.toDateTimeToday());

                result = duration.getStandardMinutes();
            }

            return result;
        }

    }

    private abstract class Delegator<T> {

        public abstract String logLine(String message, T object);

        public void log(String message, Level level, T object) {
            if (object != null) {
                switch (level) {
                    case DEBUG:
                        delegate.debug(logLine(message, object));
                        break;
                    case INFO:
                        delegate.info(logLine(message, object));
                        break;
                    case TRACE:
                        delegate.info(logLine(message, object));
                        break;
                }
            } else {
                delegate.warn("Logging error : object is null");
            }
        }

        public void log(String message, Level level, List<T> objects) {
            for (int i = 0 ; i < objects.size() ; i++) {
                T object = objects.get(i);
                log(message+"[" + i + "]", level, object);
            }
        }
    }

    private static Date computeEffectiveTime(TimestampDelay timestampDelay) {
        Date result = null;

        if (timestampDelay.getExpected() != null) {
            result = DateUtils.addMinutes(timestampDelay.getExpected(),
                    timestampDelay.getDelay().intValue());
        }

        return result;
    }

    private Delegator<Direction> directionDelegator = new Delegator<Direction>() {
        @Override
        public String logLine(String message, Direction object) {
            return new LogLineBuilder()
                    .message(object.getLibelle())
                    .expectedTrain(object.getTrain() != null && object.getTrain().getIdRailtime() != null ? Long.parseLong(object.getTrain().getIdRailtime()) : null)
                    .departureStation(object.getFrom() != null ? object.getFrom().getName() : null)
                    .arrivalStation(object.getTo() != null ? object.getTo().getName() : null)
                    .build();
        }
    };

    private Delegator<Step> stepDelegator = new Delegator<Step>() {
        @Override
        public String logLine(String message, Step object) {
            return new LogLineBuilder()
                    .message(message)
                    .departureStation(object.getStation() != null ? object.getStation().getName() : null)
                    .expectedDepartureTime(object.getTimestamp())
                    .effectiveDepartureTime(computeEffectiveTime(new TimestampDelay(object.getTimestamp(), object.getDelay())))
                    .build();
        }
    };

    private Delegator<LineStop> lineStopDelegator = new Delegator<LineStop>() {
        @Override
        public String logLine(String message, LineStop object) {
            return new LogLineBuilder()
                    .message(message)
                    .id(object.getId())
                    .date(object.getDate())
                    .expectedTrain(object.getTrain() != null ? object.getTrain().getId() : null)
                    .departureStation(object.getStation() != null ? object.getStation().getEnglishName() : null)
                    .expectedDepartureTime(object.getArrivalTime() != null ? object.getArrivalTime().getExpected() : null)
                    .expectedArrivalTime(object.getDepartureTime() != null ? object.getDepartureTime().getExpected() : null)
                    .effectiveDepartureTime(computeEffectiveTime(object.getArrivalTime()))
                    .effectiveArrivalTime(computeEffectiveTime(object.getDepartureTime()))
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
                    .expectedTrain(object.getExpectedTrain1() != null ? object.getExpectedTrain1().getId() : null)
                    .effectiveTrain(object.getEffectiveTrain1() != null ? object.getEffectiveTrain1().getId() : null)
                    .departureStation(object.getDepartureStation() != null ? object.getDepartureStation().getEnglishName() : null)
                    .arrivalStation(object.getArrivalStation() != null ? object.getArrivalStation().getEnglishName() : null)
                    .expectedDepartureTime(object.getExpectedDepartureTime())
                    .expectedArrivalTime(object.getExpectedArrivalTime())
                    .effectiveDepartureTime(object.getEffectiveDepartureTime())
                    .effectiveArrivalTime(object.getEffectiveArrivalTime())
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
            return new LogLineBuilder()
                    .message(message)
                    .departureStation(object.getStationName())
                    .expectedDepartureTime(object.getArrivalTime())
                    .expectedArrivalTime(object.getDepartureTime())
                    .effectiveDepartureTime(computeEffectiveTime(new TimestampDelay(object.getArrivalTime(), object.getArrivalDelay())))
                    .effectiveArrivalTime(computeEffectiveTime(new TimestampDelay(object.getDepartureTime(), object.getDepartureDelay())))
                    .build();
        }
    };


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
    public void info(String message, RouteLogDTO routeLog) {
        routeLogDTODelegator.log(message, Level.INFO, routeLog);
        servedStopDTODelegator.log("stops", Level.INFO, routeLog.getStops());
    }

    @Override
    public void debug(String message, RouteLogDTO routeLog) {
        routeLogDTODelegator.log(message, Level.DEBUG, routeLog);
        servedStopDTODelegator.log("stops", Level.DEBUG, routeLog.getStops());
    }

    @Override
    public void trace(String message, RouteLogDTO routeLog) {
        routeLogDTODelegator.log(message, Level.TRACE, routeLog);
        servedStopDTODelegator.log("stops", Level.TRACE, routeLog.getStops());
    }

    @Override
    public void info(String message, TwoDirections twoDirections) {
        directionDelegator.log(message, Level.INFO, twoDirections.getDeparture());
        stepDelegator.log(message, Level.INFO, twoDirections.getDeparture().getSteps());
        directionDelegator.log(message, Level.INFO, twoDirections.getArrival());
        stepDelegator.log(message, Level.INFO, twoDirections.getArrival().getSteps());
    }

    @Override
    public void debug(String message, TwoDirections twoDirections) {
        directionDelegator.log(message, Level.DEBUG, twoDirections.getDeparture());
        stepDelegator.log(message, Level.DEBUG, twoDirections.getDeparture().getSteps());
        directionDelegator.log(message, Level.DEBUG, twoDirections.getArrival());
        stepDelegator.log(message, Level.DEBUG, twoDirections.getArrival().getSteps());
    }

    @Override
    public void trace(String message, TwoDirections twoDirections) {
        directionDelegator.log(message, Level.TRACE, twoDirections.getDeparture());
        stepDelegator.log(message, Level.TRACE, twoDirections.getDeparture().getSteps());
        directionDelegator.log(message, Level.TRACE, twoDirections.getArrival());
        stepDelegator.log(message, Level.TRACE, twoDirections.getArrival().getSteps());
    }


    public void setDelegate(org.slf4j.Logger logger) {
        this.delegate = logger;
    }

    public void setSeparator(char separator) {
        this.separator = separator;
    }
}
