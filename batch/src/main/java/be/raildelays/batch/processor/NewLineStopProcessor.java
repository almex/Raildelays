package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.*;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainLineDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * When retrieving data from GTFS, we must aggregate with other source of data in order to get
 * a complete {@link LineStop}.
 *
 * @author Almex
 * @since 2.0
 */

public class NewLineStopProcessor implements ItemProcessor<Trip, LineStop>, InitializingBean {

    private ItemStreamReader<StopTime> stopTimesReader;
    private ItemStreamReader<CalendarDate> calendarDatesReader;
    private ItemStreamReader<Stop> stopsReader;
    private TrainLineDao trainLineDao;
    private StationDao stationDao;
    private LocalDate date;
    private Language lang;

    private static Map<ItemStreamReader<?>, List<?>> cache = new WeakHashMap<>(3);
    private static final Logger LOGGER = LoggerFactory.getLogger("Stop", NewLineStopProcessor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(lang, "The property 'lang' is mandatory");
        Assert.notNull(date, "The property 'date' is mandatory");
        Assert.notNull(trainLineDao, "The property 'trainLineDao' is mandatory");
        Assert.notNull(stopsReader, "The property 'stopsReader' is mandatory");
        Assert.notNull(stopTimesReader, "The property 'stopTimesReader' is mandatory");
        Assert.notNull(calendarDatesReader, "The property 'calendarDatesReader' is mandatory");
    }

    @Override
    public LineStop process(Trip item) throws Exception {
        LineStop result = null; // By default we filter it

        if (isScheduled(item.getServiceId())) {
            result = buildLineStop(item);

            LOGGER.info("Scheduled", item);
        } else {
            LOGGER.trace("NotScheduled", item);
        }

        LOGGER.trace("result", result);

        return result;
    }

    private LineStop buildLineStop(Trip item) {
        LineStop.Builder result = null;
        List<StopTime> stopTimes = findStopTimes(item.getTripId());

        for (StopTime stopTime : stopTimes) {
            Stop stop = findStop(stopTime.getStopId());

            if (stop != null) {
                LineStop.Builder current = new LineStop.Builder()
                        .trainLine(getTrainLine(item))
                        .arrivalTime(TimeDelay.of(stopTime.getArrivalTime()))
                        .departureTime(TimeDelay.of(stopTime.getDepartureTime()))
                        .station(getStation(stop))
                        .date(date);

                if (result == null) {
                    result = current;

                    LOGGER.debug("new", current.build(false));
                } else {
                    result.addNext(current);

                    LOGGER.trace("next", current.build(false));
                }
            }
        }

        return result != null ? result.build(false) : null;
    }

    private TrainLine getTrainLine(Trip item) {
        Long routeId = GtfsFiledSetMapper.parseRouteId(item.getRouteId());
        TrainLine result = trainLineDao.findByRouteId(routeId);

        if (result == null) {
            result = new TrainLine.Builder(routeId).build();
        }

        return result;
    }

    private Station getStation(Stop stop) {
        Station result = null;

        if (stop != null) {
            String stationName = stop.getStopName();
            int index = stationName.indexOf("/");

            if (index > -1) {
                switch (lang) {
                    case FR:
                        stationName = stationName.substring(index + 1);
                        result = stationDao.findByFrenchName(stationName);
                        break;
                    case NL:
                        stationName = stationName.substring(0, index);
                        result = stationDao.findByDutchName(stationName);
                        break;
                    case EN:
                    default:
                }
            } else {
                result = stationDao.findByEnglishName(stationName);
            }

            if (result == null) {
                result = new Station(stationName, lang);
            }
        }

        return result;
    }

    private Stop findStop(String stopId) {
        return readAll(stopsReader)
                .parallelStream()
                .filter(stop -> stop.getStopId().equals(stopId))
                .findFirst()
                .orElse(null);
    }

    private List<StopTime> findStopTimes(String tripId) {
        return readAll(stopTimesReader)
                .parallelStream()
                .filter(stopTime -> stopTime.getTripId().equals(tripId))
                .collect(Collectors.toList());
    }

    private boolean isScheduled(String serviceId) {
        return readAll(calendarDatesReader)
                .parallelStream()
                .filter(calendarDate -> calendarDate.getServiceId().equals(serviceId))
                .anyMatch(calendarDate -> calendarDate.isIncluded(this.date));
    }

    @SuppressWarnings("unchecked") // The caching doesn't have to know the type
    private static <O> List<O> readAll(ItemStreamReader<O> reader) {
        List<O> result = (List<O>) cache.get(reader);

        // Cache Manager
        if (result == null) {
            result = doReadAll(reader);
            cache.put(reader, result);

            LOGGER.debug("NewCache", !result.isEmpty() ? result.get(0) : null);
        } else {
            LOGGER.debug("HitCache", !result.isEmpty() ? result.get(0) : null);
        }

        return result;
    }

    private static <O> List<O> doReadAll(ItemStreamReader<O> reader) {
        List<O> result = Collections.synchronizedList(new ArrayList<>());

        reader.open(new ExecutionContext());

        try {
            for (O actual = reader.read(); actual != null; actual = reader.read()) {
                result.add(actual);
            }
        } catch (Exception e) {
            LOGGER.error("Error during reading of GTFS scheduled times", e);
        } finally {
            reader.close();
        }

        return result;
    }


    public void setStopTimesReader(ItemStreamReader<StopTime> stopTimesReader) {
        this.stopTimesReader = stopTimesReader;
    }

    public void setCalendarDatesReader(ItemStreamReader<CalendarDate> calendarDatesReader) {
        this.calendarDatesReader = calendarDatesReader;
    }

    public void setTrainLineDao(TrainLineDao trainLineDao) {
        this.trainLineDao = trainLineDao;
    }

    public void setStationDao(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public void setStopsReader(ItemStreamReader<Stop> stopsReader) {
        this.stopsReader = stopsReader;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }
}
