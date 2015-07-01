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

package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
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
import java.util.Locale;

/**
 * Search next train which allow you to arrive earlier to your destination.
 * This processor take into account delays from your departure station and
 * cancellation.
 *
 * @author Almex
 */
public class SearchNextTrainProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Nxt", SearchNextTrainProcessor.class);

    @Resource
    private RaildelaysService service;

    private String language = Language.EN.name();

    public static long compareTime(TimestampDelay departureB, Date departureA) {
        return compareTime(departureA, departureB.getExpected());
    }

    public static long compareTime(Date departureA, Date departureB) {
        long result = 0;

        if (departureA == null && departureB != null) {
            result = 1;
        } else if (departureA != null && departureB == null) {
            result = -1;
        } else if (departureB != null) {
            LocalTime start = new LocalTime(departureA.getTime());
            LocalTime end = new LocalTime(departureB.getTime());

            Duration duration = new Duration(start.toDateTimeToday(), end.toDateTimeToday());

            result = -duration.getMillis(); // A comparison is the opposite of a duration
        }

        return result;
    }

    public static long compareTimeAndDelay(TimestampDelay departureB, Date departureA) {
        return -compareTimeAndDelay(departureA, departureB);
    }

    public static long compareTimeAndDelay(Date departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA == null && departureB != null) {
            result = 1;
        } else if (departureA != null && departureB == null) {
            result = -1;
        } else if (departureB != null) {
            LocalTime start = new LocalTime(departureA.getTime());
            LocalTime end = new LocalTime(departureB.getExpected()).plusMinutes(departureB.getDelay().intValue());

            Duration duration = new Duration(start.toDateTimeToday(), end.toDateTimeToday());

            result = -duration.getMillis(); // A comparison is an opposite of a duration
        }

        return result;
    }

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
        List<LineStop> candidates;
        LocalDate date = new LocalDate(item.getDate());
        LocalTime time = new LocalTime(item.getExpectedArrivalTime());
        DateTime dateTime = date.toDateTime(time);
        Language lang = Language.valueOf(language.toUpperCase(Locale.US));

        LOGGER.trace("item", item);

        candidates = service.searchNextTrain(item.getArrivalStation(), dateTime.toDate());

        LOGGER.trace("candidates_arrival", candidates);

        LineStop fastestLineStop = searchFastestTrain(item, candidates);

        if (fastestLineStop != null) {
            BatchExcelRowMapperProcessor processor = new BatchExcelRowMapperProcessor();

            switch (lang) {
                case EN:
                    processor.setStationA(item.getDepartureStation().getEnglishName());
                    processor.setStationB(item.getArrivalStation().getEnglishName());
                    break;
                case FR:
                    processor.setStationA(item.getDepartureStation().getFrenchName());
                    processor.setStationB(item.getArrivalStation().getFrenchName());
                    break;
                case NL:
                    processor.setStationA(item.getDepartureStation().getDutchName());
                    processor.setStationB(item.getArrivalStation().getDutchName());
                    break;
            }
            processor.setLanguage(lang.name());

            BatchExcelRow fastestExcelRow = processor.process(fastestLineStop);

            LOGGER.info("fastest_train", fastestExcelRow);

            result = aggregate(item, fastestExcelRow);

            LOGGER.info("aggregate_result", result);
        }

        LOGGER.trace("result", result);

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

    private LineStop searchFastestTrain(BatchExcelRow item, List<LineStop> candidates) {
        LineStop fastestTrain = null;

		/*
         * The only delay that we can take into account is the one from the
		 * departure station. When you have to decide to take another train
		 * you don't know the effective arrival time.
		 */

        for (LineStop candidateArrival : candidates) {
            LineStop candidateDeparture = searchDepartureLineStop(candidateArrival, item.getDepartureStation());

            LOGGER.debug("candidate_departure", candidateDeparture);

            // We don't process null values
            if (candidateDeparture == null) {
                LOGGER.trace("filter_null_departure", candidateDeparture);
                continue;
            }

            if (candidateArrival == null) {
                LOGGER.trace("filter_null_arrival", candidateArrival);
                continue;
            }

            /*
             * Normally we don't need to go recursively into each LineStop of the chain to check cancellation.
             * If our start point is not canceled and than our stop is ever then we can take this train.
             * No matter if in between some stop are canceled.
             */
            if (candidateDeparture.isCanceledDeparture()) {
                LOGGER.trace("filter_canceled_departure", candidateDeparture);
                continue;
            }

            if (candidateArrival.isCanceledArrival()) {
                LOGGER.trace("filter_canceled_arrival", candidateArrival);
                continue;
            }

            if (item.isCanceled()) {
                LOGGER.trace("item_canceled", item);
                LOGGER.debug("faster_canceled_train_arrival", candidateArrival);
                fastestTrain = candidateArrival;
                break; // candidate arrives before item
            }


            /*
             * Do not take into account candidate which leaves after the item.
             */
            if (compareTimeAndDelay(candidateDeparture.getDepartureTime(), item.getEffectiveDepartureTime()) > 0) {
                LOGGER.trace("filter_after_departure", candidateDeparture);
                continue; // candidate leaves after item
            }

            /*
             * A candidate is faster if its expected arrival time minus the actual item delay at departure
             * is before the expected arrival of the item. Or in other words, if the difference between the candidate expected arrival time
             * and the item expected arrival time is lower than the difference between the effective and the expected departure
             * time of the item (its delay).
             */
            if (compareTime(candidateArrival.getArrivalTime(), item.getExpectedArrivalTime()) <
                    compareTime(item.getEffectiveDepartureTime(), item.getExpectedDepartureTime())) {
                LOGGER.debug("faster_delay_train_arrival", candidateArrival);
                fastestTrain = candidateArrival;
                break; // candidate arrives before item
            }
        }

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

    public void setService(RaildelaysService service) {
        this.service = service;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
