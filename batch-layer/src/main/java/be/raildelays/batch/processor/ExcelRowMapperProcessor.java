package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.bean.BatchExcelRow.Builder;
import be.raildelays.batch.exception.ArrivalDepartureEqualsException;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;

public class ExcelRowMapperProcessor implements
        ItemProcessor<LineStop, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Xls", ExcelRowMapperProcessor.class);

    private String stationA;

    private String stationB;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(stationA, "Station A name is mandatory");
        Validate.notNull(stationB, "Station B name is mandatory");
    }

    @Override
    public BatchExcelRow process(final LineStop item) throws Exception {
        BatchExcelRow result = null;

        LOGGER.trace("item", item);

        result = extractSens(item, stationA, stationB);

        LOGGER.trace("result", result);

        return result;
    }

    protected BatchExcelRow extractSens(LineStop item,
                                        String stationAName, String stationBName) throws ArrivalDepartureEqualsException {
        Station stationA = new Station(stationAName);
        Station stationB = new Station(stationBName);
        Sens sens = null;

        LineStop departure = readPrevious(item.getPrevious(), stationA,
                stationB);
        LineStop arrival = readNext(item.getNext(), stationA, stationB);

        if (departure == null) {
            departure = item;
        }

        if (arrival == null) {
            arrival = item;
        }

        if (arrival == departure) {
            throw new ArrivalDepartureEqualsException(
                    "Arrival must not be equal to departure");
        }

        LOGGER.debug("departure", departure);
        LOGGER.debug("arrival", arrival);

        if (departure.getStation().equals(stationA)
                && arrival.getStation().equals(stationB)) {
            sens = Sens.DEPARTURE;
        } else if (departure.getStation().equals(stationB)
                && arrival.getStation().equals(stationA)) {
            sens = Sens.ARRIVAL;
        }

        return map(departure, arrival, sens);
    }

    protected LineStop readPrevious(LineStop lineStop, Station stationA,
                                    Station stationB) {
        LineStop result = null;

        if (lineStop != null) {
            if (lineStop.getStation().equals(stationA)) {
                result = lineStop;
            } else if (lineStop.getStation().equals(stationB)) {
                result = lineStop;
            } else if (lineStop.getPrevious() != null) {
                result = readPrevious(lineStop.getPrevious(), stationA,
                        stationB);
            }
        }

        LOGGER.trace("extracted_left", result);

        return result;
    }

    protected LineStop readNext(LineStop lineStop, Station stationA,
                                Station stationB) {
        LineStop result = null;

        if (lineStop != null) {
            if (lineStop.getStation().equals(stationA)) {
                result = lineStop;
            } else if (lineStop.getStation().equals(stationB)) {
                result = lineStop;
            } else if (lineStop.getNext() != null) {
                result = readNext(lineStop.getNext(), stationA, stationB);
            }
        }

        LOGGER.trace("extracted_right", result);

        return result;
    }

    protected BatchExcelRow map(LineStop lineStopFrom, LineStop lineStopTo, Sens sens) {
        Date effectiveDepartureTime = computeEffectiveTime(lineStopFrom
                .getDepartureTime());
        Date effectiveArrivalTime = computeEffectiveTime(lineStopTo
                .getArrivalTime());

        BatchExcelRow result = null;

        switch (sens) {
            case DEPARTURE:
                result = new Builder(lineStopFrom.getDate(), sens) //
                        .departureStation(lineStopFrom.getStation()) //
                        .arrivalStation(lineStopTo.getStation()) //
                        .expectedDepartureTime(
                                lineStopFrom.getDepartureTime().getExpected()) //
                        .expectedArrivalTime(
                                lineStopTo.getArrivalTime().getExpected()) //
                        .expectedTrain1(lineStopFrom.getTrain()) //
                        .effectiveDepartureTime(effectiveDepartureTime) //
                        .effectiveArrivalTime(effectiveArrivalTime) //
                        .effectiveTrain1(lineStopTo.getTrain()) //
                        .delay(lineStopTo.getArrivalTime().getDelay()) //
                        .canceled(lineStopTo.isCanceled() || lineStopFrom.isCanceled())
                        .build();

                break;
            case ARRIVAL:
                result = new Builder(lineStopFrom.getDate(), sens) //
                        .departureStation(lineStopFrom.getStation()) //
                        .arrivalStation(lineStopTo.getStation()) //
                        .expectedDepartureTime(
                                lineStopFrom.getDepartureTime().getExpected()) //
                        .expectedArrivalTime(
                                lineStopTo.getArrivalTime().getExpected()) //
                        .expectedTrain1(lineStopFrom.getTrain()) //
                        .effectiveDepartureTime(effectiveDepartureTime) //
                        .effectiveArrivalTime(effectiveArrivalTime) //
                        .effectiveTrain1(lineStopTo.getTrain()) //
                        .delay(lineStopTo.getArrivalTime().getDelay()) //
                        .canceled(lineStopTo.isCanceled() || lineStopFrom.isCanceled())
                        .build();

                break;
            default:
                result = new Builder(lineStopFrom.getDate(), sens).build();

                break;
        }

        return result;
    }

    protected static Date computeEffectiveTime(TimestampDelay timestampDelay) {
        Date result = null;

        if (timestampDelay.getExpected() != null) {
            result = DateUtils.addMinutes(timestampDelay.getExpected(),
                    timestampDelay.getDelay().intValue());
        }

        return result;
    }

    public void setStationA(String stationA) {
        this.stationA = stationA;
    }

    public void setStationB(String stationB) {
        this.stationB = stationB;
    }

}
