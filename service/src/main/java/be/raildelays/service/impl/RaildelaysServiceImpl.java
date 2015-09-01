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

package be.raildelays.service.impl;

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.RailtimeTrainDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import be.raildelays.service.RaildelaysService;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * We match Train by Railtime id.<br/>
 * We match Station by English name.<br/>
 * We retrieve {@link LineStop} to check if they already exist before doing a
 * request.
 *
 * @author Almex.
 */
@Service(value = "raildelaysService")
public class RaildelaysServiceImpl implements RaildelaysService {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RaildelaysServiceImpl.class);
    @Resource
    private LineStopDao lineStopDao;
    @Resource
    private RailtimeTrainDao railtimeTrainDao;
    @Resource
    private TrainDao trainDao;
    @Resource
    private StationDao stationDao;
    @Resource
    private Mapper mapper;
    @Resource
    private Validator validator;

    static private LineStop removeEffectiveInformation(LineStop lineStop) {
        LineStop result = lineStop;

        result.setArrivalTime(removeDelay(lineStop.getArrivalTime()));
        result.setDepartureTime(removeDelay(lineStop.getDepartureTime()));

        //-- Modify backward
        LineStop previous = lineStop.getPrevious();
        while (previous != null) {
            previous.setArrivalTime(removeDelay(previous.getArrivalTime()));
            previous.setDepartureTime(removeDelay(previous.getDepartureTime()));
            previous = previous.getPrevious();
        }

        //-- Modify forward
        LineStop next = lineStop.getNext();
        while (next != null) {
            next.setArrivalTime(removeDelay(next.getArrivalTime()));
            next.setDepartureTime(removeDelay(next.getDepartureTime()));
            next = next.getNext();
        }

        return result;
    }

    static private TimeDelay removeDelay(TimeDelay timeDelay) {
        TimeDelay result = null;

        if (timeDelay != null) {
            result = timeDelay.withDelay(0L);
        }

        return result;
    }

    @Override
    @Transactional
    public List<LineStop> saveRouteLog(final RouteLogDTO routeLog) {
        LOGGER.debug("Saving route log for train={}, date={} and lang={}...",
                routeLog.getTrainId(), routeLog.getDate(), routeLog.getLanguage());

        // -- Validate our inputs
        validator.validate(routeLog);

        return persist(routeLog.getDate(),
                routeLog.getTrainId(),
                routeLog.getLanguage(),
                routeLog.getStops());
    }

    private List<LineStop> persist(final LocalDate date,
                                   final String trainId,
                                   final Language language,
                                   List<? extends ServedStopDTO> stops) {
        List<LineStop> result = new ArrayList<>();
        LineStop previous = null;

        for (ServedStopDTO stop : stops) {
            LineStop current = saveServedStop(date, trainId, language, stop, previous);

            result.add(current);

            // -- We are making the link to keep trace of direction
            previous = current;
        }

        return result;
    }

    private LineStop saveServedStop(final LocalDate date,
                                    final String trainId,
                                    final Language language,
                                    final ServedStopDTO stop,
                                    final LineStop previous) {

        LOGGER.debug("Saving timetable for train={}, date={} and stop={}...",
                new Object[]{trainId, date, stop});

        // -- Validate our inputs
        Assert.notNull(date, "You should provide a date for this served stop");
        Assert.hasText(trainId,
                "You should provide a train id for this served stop");
        validator.validate(stop);

        // -- Retrieve persisted version of sub-entities to avoid duplicate key
        Train persistedTrain = saveOrRetrieveTrain(new Train(
                trainId, language));
        Station persistedStation = saveOrRetrieveStation(new Station(
                stop.getStationName(), language));
        TimeDelay arrivalTime = TimeDelay.of(stop.getArrivalTime(), stop.getArrivalDelay());
        TimeDelay departureTime = TimeDelay.of(stop.getDepartureTime(), stop.getDepartureDelay());

        LineStop lineStop = new LineStop.Builder() //
                .date(date) //
                .station(persistedStation) //
                .train(persistedTrain) //
                .arrivalTime(arrivalTime) //
                .departureTime(departureTime) //
                .canceledArrival(stop.isCanceled()) //
                .canceledDeparture(stop.isCanceled())
                .addPrevious(previous) //
                .build();

        // -- Validate our output
        validator.validate(lineStop);

        return createOrUpdate(lineStop);
    }

    private LineStop createOrUpdate(LineStop lineStop) {
        Train train = saveOrRetrieveTrain(lineStop.getTrain());
        Station station = saveOrRetrieveStation(lineStop.getStation());
        LineStop persistedLineStop = lineStopDao.findByTrainAndDateAndStation(
                train, lineStop.getDate(), station);

        if (persistedLineStop != null) {
            persistedLineStop.setDepartureTime(lineStop.getDepartureTime());
            persistedLineStop.setArrivalTime(lineStop.getArrivalTime());
            persistedLineStop.setCanceled(lineStop.isCanceled());
            persistedLineStop.setDate(lineStop.getDate());
            persistedLineStop.setStation(station);
            persistedLineStop.setTrain(train);

            LOGGER.debug("We update a LineStop={}.", persistedLineStop);
        } else {
            persistedLineStop = lineStop.clone();
            persistedLineStop.setStation(station);
            persistedLineStop.setTrain(train);

            LOGGER.debug("We create a new LineStop={}.", persistedLineStop);
        }

        //-- Update previous reference key
        if (lineStop.getPrevious() != null && lineStop.getPrevious().getId() != null) {
            LineStop previous = lineStopDao.findOne(lineStop.getPrevious().getId());

            previous.setNext(persistedLineStop);

            LOGGER.debug("We go to previous LineStop={}.", previous);
        }

        //-- Update next reference key
        if (lineStop.getNext() != null && lineStop.getNext().getId() != null) {
            LineStop next = lineStopDao.findOne(lineStop.getNext().getId());

            next.setPrevious(persistedLineStop);

            LOGGER.debug("We go to next LineStop={}.", next);
        }

        return lineStopDao.saveAndFlush(persistedLineStop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LineStop> searchDelaysBetween(LocalDate date, Station stationA,
                                              Station stationB, long delayThreshold) {
        List<LineStop> result = new ArrayList<>();

        LOGGER.info("Searching line stops for date={} stationA={} stationB={} delayThreshold={}",
                new Object[]{date, stationA, stationB, delayThreshold});

        result.addAll(lineStopDao.findArrivalDelays(date, stationA,
                delayThreshold, null).getContent());

        LOGGER.debug("Retrieved {} line stops from station A", result.size());

        result.addAll(lineStopDao.findArrivalDelays(date, stationB,
                delayThreshold, null).getContent());

        LOGGER.debug("Retrieved {} line stops from station A and station B",
                result.size());

        LOGGER.trace("Retrieved line stops =", result.toString());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> searchAllDates(LocalDate from, LocalDate to) {
        List<LocalDate> result = null;

        if (from == null && to == null) {
            result = lineStopDao.findAllUniqueDates();
        } else {
            result = lineStopDao.findAllUniqueDates(from, to);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocalDate> searchAllDates(LocalDate lastDate) {
        List<LocalDate> result = null;

        if (lastDate == null) {
            result = lineStopDao.findAllUniqueDates();
        } else {
            result = lineStopDao.findAllUniqueDates(lastDate);
        }

        return result;
    }

    private Train saveOrRetrieveTrain(Train train) {
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
            validator.validate(train);
            result = trainDao.save(train);
        }

        return result;
    }

    private Station saveOrRetrieveStation(Station station) {
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
            validator.validate(station);
            result = stationDao.save(station);
        }

        return result;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LineStop> searchNextTrain(Station station, LocalDateTime dateTime) {
        return lineStopDao.findNextExpectedArrivalTime(station, dateTime);
    }

    @Override
    public LineStop searchScheduledLine(Train train, Station station) {
        LineStop result = null;

        LineStop scheduledLineStop = lineStopDao.findFistScheduledLine(train, station);

        if (scheduledLineStop != null) {
            result = removeEffectiveInformation(scheduledLineStop);
        }

        return result;
    }

    @Override
    public LineStop searchLineStopByTrain(Long trainId, LocalDate date) {
        LineStop result = null;

        if (trainId != null && date != null) {
            result = lineStopDao.findByTrainIdAndDate(trainId, date);
        }

        return result;
    }

}
