package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.logger.Logger;
import be.raildelays.logger.LoggerFactory;
import be.raildelays.service.RaildelaysService;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Search next train which allow you to arrive earlier to your destination.
 * This processor take into account delays from your departure station and
 * cancellation.
 * 
 * @author Almex
 */
public class SearchNextTrainProcessor implements
		ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Nxt", SearchNextTrainProcessor.class);

	@Resource
	private RaildelaysService service;

	@Override
	public void afterPropertiesSet() throws Exception {

	}

	public List<BatchExcelRow> process(List<BatchExcelRow> items) throws Exception {
		List<BatchExcelRow> result = new ArrayList<>();
		
		for (BatchExcelRow item : items) {
			result.add(process(item));	
		}
		
		return result;
	}

    @Override
	public BatchExcelRow process(final BatchExcelRow item) throws Exception {
		BatchExcelRow result = item; // By default we return the item itself
		List<LineStop> candidates = new ArrayList<>();
		LocalDate date = new LocalDate(item.getDate());
		LocalTime time = new LocalTime(item.getExpectedArrivalTime());
		DateTime dateTime = date.toDateTime(time);

		LOGGER.trace("item", item);
		
		candidates = service.searchNextTrain(item.getArrivalStation(), dateTime.toDate());

		LOGGER.trace("candidates", candidates);

		LineStop fastestTrain = searchFastestTrain(item, candidates);

		if (fastestTrain != null) {
			//result = aggregate(item, searchDepartureLineStop(fastestTrain, item.getDepartureStation()), fastestTrain);
            ExcelRowMapperProcessor processor = new ExcelRowMapperProcessor();

            processor.setStationA(item.getDepartureStation().getEnglishName());
            processor.setStationB(item.getArrivalStation().getEnglishName());

            BatchExcelRow fasterItem = processor.process(fastestTrain);

            result = aggregate(item, fasterItem);

			LOGGER.info("aggregate_result", result);
		}

		return result;
	}

	private BatchExcelRow aggregate(final BatchExcelRow item, final BatchExcelRow fasterItem) {
		DateTime expectedArrivalTime = new DateTime(item.getExpectedArrivalTime());
		DateTime effectiveArrivalTime = new DateTime(fasterItem.getEffectiveArrivalTime());
		Duration delay = new Duration(expectedArrivalTime, effectiveArrivalTime);
		
		return new BatchExcelRow.Builder(item.getDate(), item.getSens())
				.arrivalStation(item.getArrivalStation())
				.departureStation(item.getDepartureStation())
				.expectedTrain1(item.getExpectedTrain1())
				.expectedTrain2(item.getExpectedTrain2())
				.effectiveTrain1(fasterItem.getEffectiveTrain1())
                		.effectiveTrain2(fasterItem.getEffectiveTrain2())
				.expectedDepartureTime(item.getExpectedDepartureTime())
				.expectedArrivalTime(item.getExpectedArrivalTime())
                		.effectiveDepartureTime(fasterItem.getEffectiveDepartureTime())
				.effectiveArrivalTime(fasterItem.getEffectiveArrivalTime())
				.delay(delay.getMillis() / 1000 / 60)
				.build();
	}

	private LineStop searchFastestTrain(BatchExcelRow item,
			List<LineStop> candidates) {
		LineStop fastestTrain = null;		

		/*
		 * The only delay that we can take into account is the one from the 
		 * departure station. When you have to decide to take another train 
		 * you don't know the effective arrival time.
		 */
		
		for (LineStop candidate : candidates) {
            LineStop candidateDeparture = searchDepartureLineStop(candidate, item.getDepartureStation());
            LineStop candidateArrival = candidate;

            // We don't process null values
            if (candidateDeparture == null || candidateArrival == null) {
                LOGGER.trace("filter_null_departure", candidateDeparture);
                LOGGER.trace("filter_null_arrival", candidateArrival);
                continue;
            }
			
			// FIXME We must go recursively into getNext() to search any other cancellation.
            if (candidateDeparture.isCanceled() || candidateArrival.isCanceled()) {
                LOGGER.trace("filter_canceled_departure", candidateDeparture);
                LOGGER.trace("filter_canceled_arrival", candidateArrival);
                continue;
			}

			if (item.isCanceled()) {
                LOGGER.trace("item_canceled", item);
                LOGGER.debug("faster_train", candidateArrival);
                fastestTrain = candidateArrival;
                break; // candidate arrives before item
			}
			
			// Do not take into account train which leaves after the expected
			// one.
            if (compareTimeAndDelay(item.getEffectiveDepartureTime(), candidateDeparture.getDepartureTime()) >= 0) {
                LOGGER.trace("filter_after_departure", candidateDeparture);
                LOGGER.trace("filter_after_arrival", candidateArrival);
                continue; // candidate leaves after item
			}

			// expected arrivalTime to destination and using delay from departure
            if (compareTime(item.getEffectiveArrivalTime(), candidateArrival.getArrivalTime()) < 0) {
                LOGGER.debug("faster_train", candidateArrival);
                fastestTrain = candidateArrival;
                break; // candidate arrives before item
			}
		}

        LOGGER.info("fastest_train", fastestTrain);

		return fastestTrain;
	}

	private LineStop searchDepartureLineStop(LineStop lineStop, Station departureStation) {
		LineStop result = null;

		if (lineStop != null) {
			if (lineStop.getStation().equals(departureStation)) {
				result = lineStop;
			} else if (lineStop.getPrevious() != null) {
				result = searchDepartureLineStop(lineStop.getPrevious(), departureStation);
			}
		}

		return result;
	}

    public static long compareTime(Date departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA == null && departureB != null) {
            result = 1;
        } else if (departureA != null && departureB == null) {
            result = -1;
        } else {
            LocalTime start = new LocalTime(departureA.getTime());
            LocalTime end = new LocalTime(departureB.getExpected());

            Duration duration = new Duration(start.toDateTimeToday(), end.toDateTimeToday());

            result = duration.getMillis();
        }

        return result;
	}

    public static long compareTimeAndDelay(Date departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA == null && departureB != null) {
            result = 1;
        } else if (departureA != null && departureB == null) {
            result = -1;
        } else {
            LocalTime start = new LocalTime(departureA.getTime());
            LocalTime end = new LocalTime(departureB.getExpected()).plusMinutes(departureB.getDelay().intValue());

            Duration duration = new Duration(start.toDateTimeToday(), end.toDateTimeToday());

            result = duration.getMillis();
        }

        return result;
	}

	public void setService(RaildelaysService service) {
		this.service = service;
	}

}
