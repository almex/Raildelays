package be.raildelays.batch.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.service.RaildelaysService;

;

public class SearchNextTrainProcessor implements
		ItemProcessor<List<LineStop>, List<LineStop>>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchNextTrainProcessor.class);

	private String stationA;

	private String stationB;

	@Resource
	private RaildelaysService service;

	@Override
	public void afterPropertiesSet() throws Exception {
		Validate.notNull(stationA, "Station A name is mandatory");
		Validate.notNull(stationB, "Station B name is mandatory");

		LOGGER.info("Processing for stationA={} and stationB={}...", stationA,
				stationB);
	}

	@Override
	public List<LineStop> process(final List<LineStop> items) throws Exception {
		List<LineStop> result = new ArrayList<>();

		for (LineStop item : items) {
			List<LineStop> candidates = new ArrayList<>();

			if (item.isCancelled()) {

				if (item.getStation().equals(stationA)) {
					candidates = service.searchNextTrain(new Station(stationA),
							new Station(stationB), item.getDate());
				} else if (item.getStation().equals(stationB)) {
					candidates = service.searchNextTrain(new Station(stationB),
							new Station(stationA), item.getDate());
				}
			}

			result.add(searchFastestTrain(item, candidates));
		}

		return result;
	}

	private LineStop searchFastestTrain(LineStop item, List<LineStop> candidates) {
		LineStop fastestTrain = item;

		for (LineStop candidate : candidates) {			
			//TODO take into account canceled candidates recursively via getNext()
			if (candidate.isCancelled()) {
				continue;
			}

			// Do not take into account train which leaves before the one you want to take
			if (compareTimeAndDelay(candidate.getDepartureTime(), item.getDepartureTime()) > 0) {
				continue; // candidate leave after item
			}
			
			/*
			 * 16:25 (+5") faster than 16:15 (+30")
			 *  
			 * because: 
			 * 16:25 - 16:15 = 10" 
			 * and 
			 * 30" - 5" = 25" 
			 * => 
			 * 10" < 25" (faster)
			 */
			if (compareTimeAndDelay(candidate.getArrivalTime(), item.getArrivalTime()) < 0) {
				fastestTrain = candidate;
				break; // candidate arrive before item
			}
			
		}

		return fastestTrain;
	}
	
	public static long compareTimeAndDelay(TimestampDelay timeA, TimestampDelay timeB) {
		long deltaTime = timeB.getExpected().getTime() - timeA.getExpected().getTime();
		long deltaDelay = (timeB.getDelay() - timeA.getDelay()) * 1000 * 60;
		
		return deltaTime - deltaDelay;
	}

	public void setStationA(String stationA) {
		this.stationA = stationA;
	}

	public void setStationB(String stationB) {
		this.stationB = stationB;
	}

}
