package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.GtfsFiledSetMapper;
import be.raildelays.batch.gtfs.Stop;
import be.raildelays.batch.gtfs.StopTime;
import be.raildelays.batch.gtfs.Trip;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Build a {@link LineStop} based on a {@link Trip} retrieved from GTFS data.
 *
 * @author Almex
 * @since 2.0
 */
public class BuildLineStopProcessor extends AbstractGtfsDataProcessor<Trip, LineStop> implements InitializingBean {

    private ItemStreamReader<Stop> stopsReader;
    private Language lang;
    private LocalDate date;


    private static final Logger LOGGER = LoggerFactory.getLogger("Bui", BuildLineStopProcessor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(lang, "The property 'lang' is mandatory");
        Assert.notNull(date, "The property 'date' is mandatory");
        Assert.notNull(stopsReader, "The property 'stopsReader' is mandatory");
    }

    @Override
    public LineStop process(Trip item) throws Exception {
        LineStop result = null;
        LineStop.Builder builder = buildLineStop(item);

        if (builder != null) {
            result = builder.build(false);
        }

        return result;
    }

    private LineStop.Builder buildLineStop(Trip item) {
        LineStop.Builder result = null;

        for (StopTime stopTime : item.getStopTimes()) {
            Stop stop = findStop(stopTime.getStopId());

            if (stop != null && stop.getLocationType().equals(Stop.LocationType.NOT_PHYSICAL)) {
                LineStop.Builder current = new LineStop.Builder()
                        .trainLine(new TrainLine.Builder(GtfsFiledSetMapper.parseRouteId(item.getRouteId()))
                                .build(false))
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

        return result;
    }

    private Station getStation(Stop stop) {
        Station result = null;

        if (stop != null && stop.getStopName() != null) {
            String stationName = stop.getStopName();
            int index = stationName.indexOf("/");

            if (index > -1) {
                switch (lang) {
                    case FR:
                        stationName = stationName.substring(index + 1);
                        break;
                    case NL:
                        stationName = stationName.substring(0, index);
                        break;
                    case EN:
                    default:
                }
            }

            result = new Station(stationName, lang);
        }

        return result;
    }

    private Stop findStop(String stopId) {
        Stop result = null;

        if (stopId != null) {
            result = readAll(stopsReader)
                    .parallelStream()
                    .filter(stop -> stopId.equals(stop.getStopId()))
                    .findFirst()
                    .orElse(null);
        }

        return result;
    }

    public void setStopsReader(ItemStreamReader<Stop> stopsReader) {
        this.stopsReader = stopsReader;
    }

    public void setLang(Language lang) {
        this.lang = lang;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
