package be.raildelays.batch.gtfs;

import java.time.LocalDate;
import java.util.Date;

/**
 * Bean representation of GTFS calendar_dates.txt file.
 *
 * @almex Almex
 * @since 2.0
 */
public class CalendarDate {

    private String serviceId;
    private LocalDate date;
    private ExceptionType exceptionType;

    /**
     * Determine if a {@link Date} is included or excluded
     *
     * @author Almex
     * @since 2.0
     */
    public enum ExceptionType {
        ADDED, REMOVED;

        public static ExceptionType valueOfIndex(int index) {
            ExceptionType result = ExceptionType.ADDED;

            if (index == 2) {
                result = ExceptionType.REMOVED;
            }

            return result;
        }
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setExceptionType(ExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    public boolean isIncluded(LocalDate date) {
        return this.date.equals(date) && ExceptionType.ADDED.equals(exceptionType);
    }
}
