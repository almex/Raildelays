package be.raildelays.batch.processor;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.logger.Logger;
import be.raildelays.logger.LoggerFactory;
import org.apache.commons.lang.Validate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;

/**
 * Map a list of two {@link Direction} to a {@link RouteLogDTO}
 *
 * @author Almex
 */
public class RouteLogMapperProcessor implements
        ItemProcessor<TwoDirections, RouteLogDTO>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Rut", RouteLogMapperProcessor.class);

    private Date date;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(date, "Date is mandatory");
    }

    @Override
    public RouteLogDTO process(final TwoDirections item) throws Exception {
        RouteLogDTO result = null;

        LOGGER.trace("item", item);

        Direction departureDirection = item.getDeparture();
        Direction arrivalDirection = item.getArrival();

        if (departureDirection != null && arrivalDirection != null) {
            result = new RouteLogDTO(arrivalDirection.getTrain().getIdRailtime(), date);

            LOGGER.debug("new_route_log", result);

            for (Step arrivalStep : arrivalDirection.getSteps()) {
                int index = arrivalDirection.getSteps().indexOf(arrivalStep);
                Step departureStep = departureDirection.getSteps().get(index);

                ServedStopDTO stop = new ServedStopDTO(arrivalStep.getStation().getName(),
                        departureStep.getTimestamp(), departureStep.getDelay(),
                        arrivalStep.getTimestamp(), arrivalStep.getDelay(), arrivalStep.isCanceled() || departureStep.isCanceled());

                LOGGER.debug("processing_done", stop);

                result.addStop(stop);
            }
        }

        LOGGER.trace("result", result);

        return result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
