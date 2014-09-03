package be.raildelays.service.impl;

import be.raildelays.domain.Language;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
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
import java.util.ArrayList;
import java.util.Date;
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

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RaildelaysServiceImpl.class);

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

    private List<LineStop> persist(final Date date, final String trainId, final Language language,
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

    private LineStop saveServedStop(final Date date, final String trainId, final Language language,
                                    final ServedStopDTO stop, final LineStop previous) {

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
        TimestampDelay arrivalTime = new TimestampDelay(stop.getArrivalTime(),
                stop.getArrivalDelay());
        TimestampDelay departureTime = new TimestampDelay(
                stop.getDepartureTime(), stop.getDepartureDelay());
        LineStop lineStop = new LineStop.Builder() //
                .date(date) //
                .station(persistedStation) //
                .train(persistedTrain) //
                .arrivalTime(arrivalTime) //
                .departureTime(departureTime) //
                .canceled(stop.isCanceled()) //
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
    public List<LineStop> searchDelaysBetween(Date date, Station stationA,
                                              Station stationB, int delayThreshold) {
        List<LineStop> result = new ArrayList<>();

        LOGGER.info("Searching line stops for date={} stationA={} stationB={} delayThreshold={}",
                new Object[]{date, stationA, stationB, delayThreshold});

        result.addAll(lineStopDao.findArrivalDelays(date, stationA,
                delayThreshold));

        LOGGER.debug("Retrieved {} line stops from station A", result.size());

        result.addAll(lineStopDao.findArrivalDelays(date, stationB,
                delayThreshold));

        LOGGER.debug("Retrieved {} line stops from station A and station B",
                result.size());

        LOGGER.trace("Retrieved line stops =", result.toString());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Date> searchAllDates(Date from, Date to) {
        List<Date> result = null;

        if (from == null && to == null) {
            result = lineStopDao.findAllUniqueDates();
        } else {
            result = lineStopDao.findAllUniqueDates(from, to);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Date> searchAllDates(Date lastDate) {
        List<Date> result = null;

        if (lastDate == null) {
            result = lineStopDao.findAllUniqueDates();
        } else {
            result = lineStopDao.findAllUniqueDates(lastDate);
        }

        return result;
    }

//    private RailtimeTrain saveOrRetrieveRailtimeTrain(RailtimeTrain train) {
//        RailtimeTrain result = null;
//        RailtimeTrain persistedTrain = railtimeTrainDao.findByRailtimeId(train
//                .getRailtimeId());
//
//        if (persistedTrain == null) {
//            validator.validate(train);
//            result = railtimeTrainDao.save(train);
//        } else {
//            result = persistedTrain;
//        }
//
//        return result;
//    }

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
    public List<LineStop> searchNextTrain(Station station, Date date) {
        return lineStopDao.findNextExpectedArrivalTime(station, date);
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

    /**
     * Search LineStop corresponding to a train id and for a date.
     *
     * @param trainId for which you do a look-up
     * @param date    for which correspond one LineStop
     * @return null if not found or the expected LineStop
     */
    @Override
    public LineStop searchLineStopByTrain(Long trainId, Date date) {
        LineStop result = null;

        if (trainId != null && date != null) {
            result = lineStopDao.findByTrainIdAndDate(trainId, date);
        }

        return result;
    }

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

    static private TimestampDelay removeDelay(TimestampDelay timestamp) {
        TimestampDelay result = null;

        if (timestamp != null) {
            result = new TimestampDelay(timestamp.getExpected(), 0L);
        }

        return result;
    }

}
