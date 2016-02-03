package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.StopTime;
import be.raildelays.batch.gtfs.Trip;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Retrieve all {@link StopTime} that belong to a {@link Trip} from GTFS data.
 *
 * @author Almex
 * @since 2.0
 */
public class FindStopTimesProcessor extends AbstractGtfsDataProcessor<Trip, Trip> implements InitializingBean {

    private ItemStreamReader<StopTime> stopTimesReader;

    private static final Logger LOGGER = LoggerFactory.getLogger("Fin", FindStopTimesProcessor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(stopTimesReader, "The property 'stopTimesReader' is mandatory");
    }

    @Override
    public Trip process(Trip item) throws Exception {
        item.setStopTimes(findStopTimes(item.getTripId()));

        LOGGER.debug("result", item);

        return item;
    }

    private List<StopTime> findStopTimes(String tripId) {
        return readAll(stopTimesReader)
                .parallelStream()
                .filter(stopTime -> stopTime.getTripId().equals(tripId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setStopTimesReader(ItemStreamReader<StopTime> stopTimesReader) {
        this.stopTimesReader = stopTimesReader;
    }
}
