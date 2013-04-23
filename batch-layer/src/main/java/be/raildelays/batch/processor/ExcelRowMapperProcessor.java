package be.raildelays.batch.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.xls.ExcelRow;

public class ExcelRowMapperProcessor implements
		ItemProcessor<List<LineStop>, List<ExcelRow>>, InitializingBean {
        
	private static final int DELAY_THRESHOLD = 15;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemProcessor.class);

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
	public List<ExcelRow> process(final List<LineStop> items) throws Exception {
		List<ExcelRow> result = null;
		
		if (items.size() > 0) { // We remove empty list (a null returned value do not pass-through the Writer)
			result = extractSens(items, stationA, stationB);
		}	

		return result;
	}

	private List<ExcelRow> extractSens(List<LineStop> items,
			String stationAName, String stationBName) {
		List<ExcelRow> result = new ArrayList<>();
		Station stationA = new Station(stationAName);
		Station stationB = new Station(stationBName);
		Sens sens = null;

		for (LineStop lineStop : items) {
			LineStop departure = readPrevious(lineStop.getPrevious(), stationA, stationB);
			LineStop arrival = readNext(lineStop.getNext(), stationA, stationB);

			if (departure == null) {
				departure = lineStop;
			}

			if (arrival == null) {
				arrival = lineStop;
			}

			if (arrival == departure) {
				throw new IllegalStateException(
						"Arrival must not be equal to departure");
			}			

			LOGGER.trace("departure={} arrival={}", departure, arrival);

			if (departure.getStation().equals(stationA)
					&& arrival.getStation().equals(stationB)) {
				sens = Sens.DEPARTURE;
			} else if (departure.getStation().equals(stationB)
					&& arrival.getStation().equals(stationA)) {
				sens = Sens.ARRIVAL;
			}

			// We filters row a minimum delay of 15 minutes 
			if (arrival.getArrivalTime().getDelay() >= DELAY_THRESHOLD) {
				ExcelRow excelRow = map(departure, arrival, sens);
				
				result.add(excelRow);
			}
		}

		return result;
	}

	private LineStop readPrevious(LineStop lineStop, Station stationA,
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

	private LineStop readNext(LineStop lineStop, Station stationA,
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

	private ExcelRow map(LineStop lineStopFrom, LineStop lineStopTo, Sens sens) {
		ExcelRow result = new ExcelRow();
		Date effectiveDepartureHour = computeEffectiveHour(lineStopFrom
				.getDepartureTime());
		Date effectiveArrivalHour = computeEffectiveHour(lineStopTo
				.getArrivalTime());

		result.setDate(lineStopFrom.getDate());
		result.setDepartureStation(lineStopFrom.getStation());
		result.setArrivalStation(lineStopTo.getStation());
		result.setLinkStation(null);
		result.setExpectedDepartureHour(lineStopFrom.getDepartureTime()
				.getExpected());
		result.setExpectedArrivalHour(lineStopTo.getArrivalTime().getExpected());
		result.setExpectedTrain1(lineStopFrom.getTrain());
		result.setExpectedTrain2(null);
		result.setEffectiveDepartureHour(effectiveDepartureHour);
		result.setEffectiveArrivalHour(effectiveArrivalHour);
		result.setEffectiveTrain1(lineStopTo.getTrain());
		result.setEffectiveTrain2(null);
		result.setDelay(lineStopTo.getArrivalTime().getDelay());
		result.setSens(sens);

		return result;
	}
	
	private static Date computeEffectiveHour(TimestampDelay timestampDelay) {
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
