package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.CalendarDate;
import be.raildelays.batch.gtfs.Trip;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.time.LocalDate;

/**
 * Filter any unscheduled {@link Trip}.
 *
 * @author Almex
 * @since 2.0
 */
public class FilterUnscheduledTripProcessor extends AbstractGtfsDataProcessor<Trip, Trip> implements InitializingBean {

    private ItemStreamReader<CalendarDate> calendarDatesReader;
    private LocalDate date;

    private static final Logger LOGGER = LoggerFactory.getLogger("Uns", FilterUnscheduledTripProcessor.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(calendarDatesReader, "The property 'calendarDatesReader' is mandatory");
        Assert.notNull(date, "The property 'date' is mandatory");
    }

    @Override
    public Trip process(Trip item) throws Exception {
        Trip result = null;

        if (isScheduled(item.getServiceId())) {
            result = item;
        }

        LOGGER.debug("result", result);

        return result;
    }

    private boolean isScheduled(String serviceId) {
        boolean result = false;

        if (serviceId != null) {
            result = readAll(calendarDatesReader)
                    .parallelStream()
                    .filter(calendarDate -> serviceId.equals(calendarDate.getServiceId()))
                    .anyMatch(calendarDate -> calendarDate.isIncluded(this.date));
        }

        return result;
    }

    public void setCalendarDatesReader(ItemStreamReader<CalendarDate> calendarDatesReader) {
        this.calendarDatesReader = calendarDatesReader;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
