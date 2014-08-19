package be.raildelays.batch.processor;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import be.raildelays.service.RaildelaysService;
import org.springframework.batch.item.ItemProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * If one stop is not deserved (canceled) then we have no expected time. We must
 * therefore find another way to retrieve line scheduling before persisting a
 * <code>RouteLog</code>.
 *
 * @author Almex
 */
public class AggregateExpectedTimeProcessor implements ItemProcessor<LineStop, LineStop> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Agg", AggregateExpectedTimeProcessor.class);

    @Resource
    private RaildelaysService service;


    public List<LineStop> process(List<LineStop> items) throws Exception {
        List<LineStop> result = null;

        for (LineStop item : items) {
            if (result == null) {
                result = new ArrayList<>();
            }

            result.add(process(item));
        }

        return result;
    }


    @Override
    public LineStop process(LineStop item) throws Exception {
        LineStop result = item;

        LOGGER.trace("item", item);

        /*
         * I would prefer to check if it lacks some information instead of checking if it's canceled or not
         * but the thing is that Railtime gives wrong information when a line stop is canceled.
         * Sometimes it gives an expected arrival time equal to the expected departure time.
         */
        if (hasAnyCanceled(item)) {
            LOGGER.info("have_canceled_stop", item);

            LineStop.Builder builder = fetchScheduling(item);

            if (builder != null) {
                //-- Modify backward
                LineStop previous = item.getPrevious();
                while (previous != null) {
                    builder.addPrevious(fetchScheduling(previous));
                    previous = previous.getPrevious();
                }

                //-- Modify forward
                LineStop next = item.getNext();
                while (next != null) {
                    builder.addNext(fetchScheduling(next));
                    next = next.getNext();
                }

                result = builder.build();

                LOGGER.debug("after_processing", result);
            }
        }

         LOGGER.trace("result", result);

        return result;
    }


    private boolean hasAnyCanceled(LineStop item) {
        boolean result = false;

        if (item != null) {
            LineStop  previous = item;
            LineStop next = item;

            while (!result && previous != null) {
                result = next.isCanceled();
                previous = previous.getPrevious();
            }

            while (!result && next != null) {
                result = next.isCanceled();
                next = next.getNext();
            }
        }

        return result;
    }



    public LineStop.Builder fetchScheduling(LineStop item) throws Exception {
        LineStop.Builder result = new LineStop.Builder(item, false, false);
        LineStop candidate = service.searchScheduledLine(item.getTrain(), item.getStation());

        //-- If we cannot retrieve one of the expected time then this item is corrupted we must filter it.
        if (candidate == null) {
            LOGGER.trace("no_candidate", item);

            return null;
        } else {
            LOGGER.debug("candidate", candidate);
        }

         final TimestampDelay departureTime = new TimestampDelay(candidate.getDepartureTime().getExpected(), 0L);
        final TimestampDelay arrivalTime = new TimestampDelay(candidate.getArrivalTime().getExpected(), 0L);

        result.departureTime(departureTime) //
                .arrivalTime(arrivalTime);

        return result;
    }

    public void setService(RaildelaysService service) {
        this.service = service;
    }
}
