package be.raildelays.batch.processor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.service.RaildelaysService;

;

public class SearchNextTrainProcessor implements
		ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SearchNextTrainProcessor.class);

	@Resource
	private RaildelaysService service;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public BatchExcelRow process(final BatchExcelRow item) throws Exception {
		BatchExcelRow result = null;	// By default we skip a canceled train	 
		List<LineStop> candidates = new ArrayList<>();		
		
		if (item.isCanceled()) {
			candidates = service.searchNextTrain(item.getDepartureStation(),
					item.getArrivalStation(), item.getDate());
		}

		LineStop fastestTrain = searchFastestTrain(item, candidates);

		return result;
	}

	private LineStop searchFastestTrain(BatchExcelRow item, List<LineStop> candidates) {
		LineStop fastestTrain = null;

		for (LineStop candidate : candidates) {			
			//TODO take into account canceled candidates recursively via getNext()
			if (candidate.isCanceled()) {
				continue;
			}

			// Do not take into account train which leaves before the one you want to take
			if (compareTimeAndDelay(candidate.getDepartureTime(), item.getExpectedDepartureTime(), item.getDelay()) > 0) {
				continue; // candidate leaves after item
			}
			
			//FIXME we must recursively search into getNext() to retrieve expected arrivalTime to stationB and using delay from stationA
			if (compareTimeAndDelay(candidate.getArrivalTime(), item.getExpectedArrivalTime(), item.getDelay()) < 0) {
				fastestTrain = candidate;
				break; // candidate arrives before item
			}
		}

		return fastestTrain;
	}
	
	public static long compareTimeAndDelay(TimestampDelay timeA, Date timeB, Long delay) {
		long deltaTime = timeB.getTime() - timeA.getExpected().getTime();
		long deltaDelay = (delay - timeA.getDelay()) * 1000 * 60;
		
		
		
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
		return deltaTime - deltaDelay;
	}

}
