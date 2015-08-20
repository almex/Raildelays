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

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Locale;

/**
 * Transform {@link be.raildelays.domain.railtime.TwoDirections} into one {@link be.raildelays.domain.entities.LineStop}.
 * This processor also merge content of the {@link be.raildelays.domain.entities.Train} and the
 * {@link be.raildelays.domain.entities.Station} (so only read access from the database are done in this processor
 * and it does not interfere with the commit-interval).
 *
 * @author Almex
 * @since 1.1
 */
public class LineStopMapperProcessor implements ItemProcessor<TwoDirections, LineStop>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("LnS", LineStopMapperProcessor.class);

    @Resource
    private TrainDao trainDao;

    @Resource
    private StationDao stationDao;

    private Date date;

    private String language = Language.EN.name();

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public LineStop process(final TwoDirections item) throws Exception {
        LineStop result = null;
        Language lang = Language.valueOf(language.toUpperCase(Locale.US));

        LOGGER.trace("item", item);

        Direction departureDirection = item.getDeparture();
        Direction arrivalDirection = item.getArrival();

        if (departureDirection != null && arrivalDirection != null) {
            LineStop next = null;

            LOGGER.debug("departure_direction", departureDirection);
            LOGGER.debug("arrival_direction", arrivalDirection);

            for (Step arrivalStep : arrivalDirection.getSteps()) {
                int index = arrivalDirection.getSteps().indexOf(arrivalStep);
                Step departureStep = departureDirection.getSteps().get(index);

                if (result == null) {
                    result = buildLineStop(lang, arrivalDirection, arrivalStep, departureStep);
                    next = result;
                } else {
                    next.setNext(buildLineStop(lang, arrivalDirection, arrivalStep, departureStep));
                    next.getNext().setPrevious(next);
                    next = next.getNext();

                }
            }
        }

        LOGGER.trace("result", result);

        return result;
    }

    private LineStop buildLineStop(Language lang, Direction direction, Step arrivalStep, Step departureStep) {
        LineStop result;
        Train train = mergeTrain(new Train(direction.getTrain().getIdRailtime(), lang));
        Station station = mergeStation(new Station(arrivalStep.getStation().getName(), lang));
        TimeDelay arrivalTime = TimeDelay.of(arrivalStep.getDateTime(), arrivalStep.getDelay());
        TimeDelay departureTime = TimeDelay.of(departureStep.getDateTime(), departureStep.getDelay());

        result = new LineStop.Builder()
                .date(date)
                .train(train)
                .station(station)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledArrival(arrivalStep.isCanceled())
                .canceledDeparture(departureStep.isCanceled())
                .build();

        LOGGER.debug("processing_done", result);

        return result;
    }

    private Train mergeTrain(Train train) {
        Train result = null;

        if (StringUtils.isNotBlank(train.getEnglishName())) {
            result = trainDao.findByEnglishName(train.getEnglishName());
        }

        if (result == null && StringUtils.isNotBlank(train.getFrenchName())) {
            result = trainDao.findByFrenchName(train.getFrenchName());
        }

        if (result == null && StringUtils.isNotBlank(train.getDutchName())) {
            result = trainDao.findByDutchName(train.getDutchName());
        }

        if (result == null) {
            result = train;

            LOGGER.debug("create_train={}.", train);
        }

        LOGGER.trace("train={}.", result);

        return result;
    }

    private Station mergeStation(Station station) {
        Station result = null;

        if (StringUtils.isNotBlank(station.getEnglishName())) {
            result = stationDao.findByEnglishName(station.getEnglishName());
        }

        if (result == null && StringUtils.isNotBlank(station.getFrenchName())) {
            result = stationDao.findByFrenchName(station.getFrenchName());
        }

        if (result == null && StringUtils.isNotBlank(station.getDutchName())) {
            result = stationDao.findByDutchName(station.getDutchName());
        }

        if (result == null) {
            result = station;

            LOGGER.debug("create_station={}.", station);
        }

        LOGGER.trace("station={}.", result);

        return result;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setTrainDao(TrainDao trainDao) {
        this.trainDao = trainDao;
    }

    public void setStationDao(StationDao stationDao) {
        this.stationDao = stationDao;
    }
}

