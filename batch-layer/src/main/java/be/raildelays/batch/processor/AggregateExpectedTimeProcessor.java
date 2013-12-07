package be.raildelays.batch.processor;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;

import javax.annotation.Resource;

/**
 * If one stop is not deserved (canceled) then we have no expected time. We must
 * therefore find another way to retrieve line scheduling before persisting a
 * <code>RouteLog</code>.
 *
 * @author Almex
 */
public class AggregateExpectedTimeProcessor implements ItemProcessor<RouteLogDTO, RouteLogDTO> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AggregateExpectedTimeProcessor.class);

    @Resource
    private RaildelaysService service;

    @Override
    public RouteLogDTO process(RouteLogDTO item) throws Exception {
        RouteLogDTO result = new RouteLogDTO(item.getTrainId(), item.getDate());

        LOGGER.debug("RouteLogDTO before processing={}", item);

        for (ServedStopDTO stop : item.getStops())  {
            if (stop.getArrivalTime() == null || stop.getDepartureTime() == null) {
                LOGGER.info("It lacks one expected time from this stop={}", stop);

                LineStop line = service.searchScheduledLine(new Train(item.getTrainId()), new Station(stop.getStationName()));

                //-- If we cannot retrieve one of the expected time then this item is corrupted we must filter it.
                if (line == null) {
                    LOGGER.debug("We must filter this {}", item);

                    return null;
                }

                result.addStop(new ServedStopDTO(stop.getStationName(),
                        line.getDepartureTime().getExpected(),
                        stop.getDepartureDelay(),
                        line.getArrivalTime().getExpected(),
                        stop.getArrivalDelay(),
                        stop.isCanceled()));
            } else {
                result.addStop(new ServedStopDTO(stop.getStationName(),
                        stop.getDepartureTime(),
                        stop.getDepartureDelay(),
                        stop.getArrivalTime(),
                        stop.getDepartureDelay(),
                        stop.isCanceled()));
            }
        }

        LOGGER.debug("Resulting RouteLogDTO after aggregation={}", result);

        return result;
    }

}
