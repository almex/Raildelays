package be.raildelays.batch.processor;

import be.raildelays.domain.Language;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Almex
 */
public class LineStopMapperProcessor implements ItemProcessor<RouteLogDTO, List<LineStop>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("LnS", LineStopMapperProcessor.class);

    @Resource
    private LineStopDao lineStopDao;

    @Resource
    private TrainDao trainDao;

    @Resource
    private StationDao stationDao;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public List<LineStop> process(RouteLogDTO routeLog) {
        LOGGER.trace("item", routeLog);

        return merge(routeLog.getDate(),
                routeLog.getTrainId(),
                routeLog.getLanguage(),
                routeLog.getStops());
    }

    private List<LineStop> merge(final Date date, final String trainId, final Language language,
                                 List<? extends ServedStopDTO> stops) {
        List<LineStop> result = new ArrayList<>();
        LineStop previous = null;

        for (ServedStopDTO stop : stops) {
            LineStop current = mergeServedStop(date, trainId, language, stop, previous);

            result.add(current);

            // -- We are making the link to keep trace of direction
            previous = current;
        }

        LOGGER.trace("result", result);

        return result;
    }

    private LineStop mergeServedStop(final Date date, final String trainId, final Language language,
                                     final ServedStopDTO stop, final LineStop previous) {

        // -- Validate our inputs
        Assert.notNull(date, "You should provide a date for this served stop");
        Assert.hasText(trainId, "You should provide a train id for this served stop");

        // -- Retrieve persisted version of sub-entities to avoid duplicate key
        Train persistedTrain = mergeTrain(new Train(trainId, language));
        Station persistedStation = mergeStation(new Station(stop.getStationName(), language));
        TimestampDelay arrivalTime = new TimestampDelay(stop.getArrivalTime(), stop.getArrivalDelay());
        TimestampDelay departureTime = new TimestampDelay(stop.getDepartureTime(), stop.getDepartureDelay());
        LineStop lineStop = new LineStop.Builder() //
                .date(date) //
                .station(persistedStation) //
                .train(persistedTrain) //
                .arrivalTime(arrivalTime) //
                .departureTime(departureTime) //
                .canceled(stop.isCanceled()) //
                .addPrevious(previous) //
                .build();

        return createOrUpdate(lineStop);
    }

    private LineStop createOrUpdate(LineStop lineStop) {
        Train train = mergeTrain(lineStop.getTrain());
        Station station = mergeStation(lineStop.getStation());
        LineStop persistedLineStop = lineStopDao.findByTrainAndDateAndStation(train, lineStop.getDate(), station);

        if (persistedLineStop != null) {
            persistedLineStop.setDepartureTime(lineStop.getDepartureTime());
            persistedLineStop.setArrivalTime(lineStop.getArrivalTime());
            persistedLineStop.setCanceled(lineStop.isCanceled());
            persistedLineStop.setDate(lineStop.getDate());
            persistedLineStop.setStation(station);
            persistedLineStop.setTrain(train);

            LOGGER.trace("update_line_stop", persistedLineStop);
        } else {
            persistedLineStop = lineStop.clone();
            persistedLineStop.setStation(station);
            persistedLineStop.setTrain(train);

            LOGGER.trace("create_line_stop", persistedLineStop);
        }

        //-- Update previous reference key
        if (lineStop.getPrevious() != null && lineStop.getPrevious().getId() != null) {
            LineStop previous = lineStopDao.findOne(lineStop.getPrevious().getId());

            previous.setNext(persistedLineStop);

            LOGGER.trace("previous_line_stop={}.", previous);
        }

        //-- Update next reference key
        if (lineStop.getNext() != null && lineStop.getNext().getId() != null) {
            LineStop next = lineStopDao.findOne(lineStop.getNext().getId());

            next.setPrevious(persistedLineStop);

            LOGGER.trace("next_line_stop={}.", next);
        }

        return persistedLineStop;
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
}

