package be.raildelays.delays;

import java.time.*;
import java.util.Date;

/**
 * This utility class make usage of JSR-310 from JDK 8 and allow comparison between {@link java.util.Date} and
 * {@link TimeDelay}.
 * <br>
 * Example of comparisons:
 * <p>
 * <code>
 * 15:15 == 15:00+15"
 * 15:00 <  15:00+15"
 * </code>
 * </p>
 *
 * @author Almex
 * @since 2.0
 */
public final class Delays {

    /**
     * We keep an arbitrary reference to an instance of {@link LocalDate} in case of conversion from
     * {@link java.sql.Time} into {@link LocalDateTime} to be sure that it's the same date between two
     * call to {@link #toLocalTime(Date)}.
     */
    public static final LocalDate DATE = LocalDate.now();

    /**
     * Default constructor.
     */
    private Delays() {
        // No instantiation is possible.
    }

    /**
     * This method is based on {@link Comparable#compareTo(Object)} contract except that it can handle {@code null}
     * reference on left parameter and that it computes the opposite of a {@link Duration} between those two
     * {@link TimeDelay} parameters (taking into account the {@linkplain TimeDelay#getEffectiveTime() effectiveTime}).
     *
     * @param startInclusive start {@link TimeDelay}, inclusive and can be null
     * @param endExclusive end {@link TimeDelay}, exclusive and can be null
     * @return number of milliseconds between {@code endExclusive} and {@code startInclusive}, 0 if either
     * {@code endExclusive} or {@code startInclusive} is null
     * @throws DateTimeException if the seconds between the temporals cannot be obtained
     * @throws ArithmeticException if the calculation exceeds the capacity of {@code Duration}
     */
    public static long compareTimeAndDelay(TimeDelay startInclusive, TimeDelay endExclusive) {
        long result = 0;

        if (startInclusive == endExclusive) {
            result = 0;
        } else if (startInclusive != null && endExclusive != null) {
            LocalTime start = startInclusive.getEffectiveTime();
            LocalTime end = endExclusive.getEffectiveTime();
            Duration duration = Duration.between(start, end);

            result = -duration.toMillis(); // The difference is the opposite of a duration
        } else if (startInclusive != null) {
            result = 1;
        } else if (endExclusive != null) {
            result = -1;
        }

        return result;
    }

    /**
     * Compare two {@link TimeDelay} and compute duration between those two
     * (taking into account delay only from the <code>timeDelay</code>).
     *
     * @param time      a {@link LocalTime}
     * @param timeDelay a {@link TimeDelay}
     * @return number of milliseconds between <code>time</code> and <code>timeDelay</code>
     */
    public static long compareTimeAndDelay(LocalTime time, TimeDelay timeDelay) {
        return compareTimeAndDelay(TimeDelay.of(time), timeDelay);
    }

    /**
     * Compare two {@link TimeDelay} and compute duration between those two
     * (taking into account delay only from the <code>timeDelay</code>).
     *
     * @param timeDelay a {@link TimeDelay}
     * @param time      a {@link LocalTime}
     * @return number of milliseconds between <code>timeDelay</code> and <code>time</code>
     */
    public static long compareTimeAndDelay(TimeDelay timeDelay, LocalTime time) {
        return compareTimeAndDelay(timeDelay, TimeDelay.of(time));
    }

    /**
     * Compare two {@link TimeDelay} and compute duration between those two
     * without taking into account delays of both parameters.
     *
     * @param timeDelayA first {@link TimeDelay}
     * @param timeDelayB second {@link TimeDelay}
     * @return number of milliseconds between <code>timeDelayA</code> and <code>timeDelayB</code>
     */
    public static long compareTime(TimeDelay timeDelayA, TimeDelay timeDelayB) {
        return compareTimeAndDelay(timeDelayA != null ? TimeDelay.of(timeDelayA.getExpectedTime()) : null, timeDelayB != null ? TimeDelay.of(timeDelayB.getExpectedTime()) : null);
    }

    /**
     * Compare two {@link TimeDelay} and compute duration between those two
     * without taking into account delay from <code>timeDelay</code>.
     *
     * @param timeDelay a {@link TimeDelay}
     * @param time      a {@link LocalTime}
     * @return number of milliseconds between <code>timeDelay</code> and <code>time</code>
     */
    public static long compareTime(TimeDelay timeDelay, LocalTime time) {
        return compareTimeAndDelay(timeDelay != null ? TimeDelay.of(timeDelay.getExpectedTime()) : null, TimeDelay.of(time));
    }

    /**
     * Compare two {@link TimeDelay} and compute duration between those two
     * without taking into account delay from <code>timeDelay</code>.
     *
     * @param time      a {@link LocalTime}
     * @param timeDelay a {@link TimeDelay}
     * @return number of milliseconds between <code>time</code> and <code>timeDelay</code>
     */
    public static long compareTime(LocalTime time, TimeDelay timeDelay) {
        return compareTimeAndDelay(TimeDelay.of(time), timeDelay != null ? TimeDelay.of(timeDelay.getExpectedTime()) : null);
    }

    /**
     * Compare two {@link LocalTime} and compute duration between those two.
     *
     * @param timeA a {@link LocalTime}
     * @param timeB a {@link LocalTime}
     * @return number of milliseconds between <code>timeA</code> and <code>timeB</code>
     */
    public static long compareTime(LocalTime timeA, LocalTime timeB) {
        return compareTimeAndDelay(TimeDelay.of(timeA), TimeDelay.of(timeB));
    }

    /**
     * Compute a delay between two {@link LocalTime}.
     * This method give a way to translate from {@link java.time} API into our Domain-specific language based on
     * {@link TimeDelay}.
     *
     * @param expectedTime  the expected time
     * @param effectiveTime the effective time
     * @return the number of milliseconds between the {@code expectedTime} and the {@code effectiveTime},
     * 0 if the {@code expectedTime} or the {@code expectedTime} is {@code null}.
     */
    public static long computeDelay(LocalTime expectedTime, LocalTime effectiveTime) {
        long result = 0;

        if (expectedTime != null && effectiveTime != null) {
            Duration duration = Duration.between(expectedTime, effectiveTime);

            result = duration.toMillis();
        }

        return result;
    }

    /**
     * Assumes translation of {@link Date} into {@link LocalTime}.
     * This implementation takes into account 4 possible types :
     * <ul>
     * <li><code>java.sql.Time</code></li>
     * <li><code>java.sql.Date</code></li>
     * <li><code>java.sql.Timestamp</code></li>
     * <li><code>java.util.Date</code></li>
     * </ul>
     *
     * @param date the date to translate
     * @return the time part of this date-time, not null
     * @throws UnsupportedOperationException if the {@code date} is an instance of {@link java.sql.Date}
     */
    public static LocalTime toLocalTime(Date date) {
        LocalTime result;

        if (date instanceof java.sql.Time) {
            result = ((java.sql.Time) date).toLocalTime();
        } else if (date instanceof java.sql.Date) {
            throw new UnsupportedOperationException("Cannot handle java.sql.Date because we expect only a time");
        } else if (date instanceof java.sql.Timestamp) {
            result = ((java.sql.Timestamp) date)
                    .toLocalDateTime()
                    .toLocalTime();
        } else {
            result = LocalDateTime
                    .ofInstant(date.toInstant(), ZoneId.systemDefault())
                    .toLocalTime();
        }

        return result;
    }
}