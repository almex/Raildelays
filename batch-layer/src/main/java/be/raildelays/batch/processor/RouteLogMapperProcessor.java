package be.raildelays.batch.processor;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.TwoDirections;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Map a list of two {@link Direction} to a {@link RouteLogDTO}
 *
 * @author Almex
 */
public class RouteLogMapperProcessor implements
        ItemProcessor<TwoDirections, RouteLogDTO>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RouteLogMapperProcessor.class);

    private Date date;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(date, "Date is mandatory");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        LOGGER.info("Processing for date={}...", sdf.format(date));
    }

    @Override
    public RouteLogDTO process(final TwoDirections item) throws Exception {
        RouteLogDTO result = null;

        Direction departureDirection = item.getDeparture();
        Direction arrivalDirection = item.getArrival();

        if (departureDirection != null && arrivalDirection != null) {
            result = new RouteLogDTO(arrivalDirection.getTrain().getIdRailtime(), date);

            for (Step arrivalStep : arrivalDirection.getSteps()) {
                int index = arrivalDirection.getSteps().indexOf(arrivalStep);
                Step departureStep = departureDirection.getSteps().get(index);

                ServedStopDTO stop = new ServedStopDTO(arrivalStep.getStation().getName(),
                        departureStep.getTimestamp(), departureStep.getDelay(),
                        arrivalStep.getTimestamp(), arrivalStep.getDelay(), arrivalStep.isCanceled() || departureStep.isCanceled());

                LOGGER.trace("Processing done for stop={}", stop);

                result.addStop(stop);
            }
        }

        return result;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
