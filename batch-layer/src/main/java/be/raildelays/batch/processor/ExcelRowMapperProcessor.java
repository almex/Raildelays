package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.bean.BatchExcelRow.Builder;
import be.raildelays.batch.exception.ArrivalDepartureEqualsException;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExcelRowMapperProcessor implements
		ItemProcessor<List<LineStop>, List<BatchExcelRow>>, InitializingBean {

	private static final int DELAY_THRESHOLD = 15;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ExcelRowMapperProcessor.class);

	private String stationA;

	private String stationB;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(stationA, "Station A name is mandatory");
		Validate.notNull(stationB, "Station B name is mandatory");

		LOGGER.info("Processing for stationA={} and stationB={}...", stationA,
				stationB);
	}

	@Override
	public List<BatchExcelRow> process(final List<LineStop> items) throws Exception {
		List<BatchExcelRow> result = null;
		List<BatchExcelRow> temp = extractSens(items, stationA, stationB);

		if (temp.size() > 0) { // We remove empty list (a null returned value do
								// not pass-through the Writer)
			result = temp;
		}

		return result;
	}

	protected List<BatchExcelRow> extractSens(List<LineStop> items,
			String stationAName, String stationBName) throws ArrivalDepartureEqualsException {
		List<BatchExcelRow> result = new ArrayList<>();
		Station stationA = new Station(stationAName);
		Station stationB = new Station(stationBName);
		Sens sens = null;

		for (LineStop lineStop : items) {
			LineStop departure = readPrevious(lineStop.getPrevious(), stationA,
					stationB);
			LineStop arrival = readNext(lineStop.getNext(), stationA, stationB);

			if (departure == null) {
				departure = lineStop;
			}

			if (arrival == null) {
				arrival = lineStop;
			}

			if (arrival == departure) {
                throw new ArrivalDepartureEqualsException(
                        "Arrival must not be equal to departure");
            }

			LOGGER.debug("departure={} arrival={}", departure, arrival);

			if (departure.getStation().equals(stationA)
					&& arrival.getStation().equals(stationB)) {
				sens = Sens.DEPARTURE;
			} else if (departure.getStation().equals(stationB)
					&& arrival.getStation().equals(stationA)) {
				sens = Sens.ARRIVAL;
			}

            BatchExcelRow excelRow = map(departure, arrival, sens);

            /*
             * It's possible that we have processed the same ExcelRow by following the path from departureStation and
             * another one from arrivalStation.
             */
            if (!result.contains(excelRow)) {
                result.add(excelRow);

                LOGGER.trace("excelRow={}", excelRow);
            }
        }

		return result;
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

		LOGGER.trace("Extracted from left={}", result);

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

		LOGGER.trace("Extracted from rigth={}", result);

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
