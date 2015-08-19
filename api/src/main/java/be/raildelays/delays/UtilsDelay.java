package be.raildelays.delays;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * This utility class make usage of JSR-310 from JDK 8 and allow comparison between {@link java.util.Date} and
 * {@link be.raildelays.delays.TimestampDelay}.
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
public final class UtilsDelay {

    /**
     * Default constructor.
     */
    private UtilsDelay() {
        // No instantiation is possible.
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * (taking into account delays of both parameters).
     *
     * @param timestampA first {@link be.raildelays.delays.TimestampDelay}
     * @param timestampB second {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>timestampA</code> and <code>timestampB</code>
     */
    public static long compareTimeAndDelay(TimestampDelay timestampA, TimestampDelay timestampB) {
        long result = 0;

        if (timestampA != null && timestampB != null) {
            if (timestampA.getExpectedTime() != null && timestampB.getExpectedTime() != null) {
                LocalDateTime start = timestampA.toLocalDateTime();
                LocalDateTime end = timestampB.toLocalDateTime();
                Duration duration = Duration.between(start, end);

                result = -duration.toMillis(); // The difference is the opposite of a duration
            } else if (timestampA.getExpectedTime() != null) {
                result = 1;
            } else if (timestampB.getExpectedTime() != null) {
                result = -1;
            }
        } else if (timestampA != null) {
            result = 1;
        } else if (timestampB != null) {
            result = -1;
        }

        return result;
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * (taking into account delay only from the <code>timestamp</code>).
     *
     * @param date      a {@link java.util.Date}
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>date</code> and <code>timestamp</code>
     */
    public static long compareTimeAndDelay(Date date, TimestampDelay timestamp) {
        return compareTimeAndDelay(TimestampDelay.of(date), timestamp);
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * (taking into account delay only from the <code>timestamp</code>).
     *
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @param date      a {@link java.util.Date}
     * @return number of milliseconds between <code>timestamp</code> and <code>date</code>
     */
    public static long compareTimeAndDelay(TimestampDelay timestamp, Date date) {
        return compareTimeAndDelay(timestamp, TimestampDelay.of(date));
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delays of both parameters.
     *
     * @param timestampA first {@link be.raildelays.delays.TimestampDelay}
     * @param timestampB second {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>timestampA</code> and <code>timestampB</code>
     */
    public static long compareTime(TimestampDelay timestampA, TimestampDelay timestampB) {
        return compareTimeAndDelay(timestampA != null ? TimestampDelay.of(timestampA.getExpectedTime()) : null, timestampB != null ? TimestampDelay.of(timestampB.getExpectedTime()) : null);
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delay from <code>timestamp</code>.
     *
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @param date      a {@link java.util.Date}
     * @return number of milliseconds between <code>timestamp</code> and <code>date</code>
     */
    public static long compareTime(TimestampDelay timestamp, Date date) {
        return compareTimeAndDelay(timestamp != null ? TimestampDelay.of(timestamp.getExpectedTime()) : null, TimestampDelay.of(date));
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delay from <code>timestamp</code>.
     *
     * @param date      a {@link java.util.Date}
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>date</code> and <code>timestamp</code>
     */
    public static long compareTime(Date date, TimestampDelay timestamp) {
        return compareTimeAndDelay(TimestampDelay.of(date), timestamp != null ? TimestampDelay.of(timestamp.getExpectedTime()) : null);
    }

    /**
     * Compare two {@link Date} and compute duration between those two.
     *
     * @param dateA a {@link Date}
     * @param dateB a {@link Date}
     * @return number of milliseconds between <code>dateA</code> and <code>dateB</code>
     */
    public static long compareTime(Date dateA, Date dateB) {
        return compareTimeAndDelay(TimestampDelay.of(dateA), TimestampDelay.of(dateB));
    }

    /**
     * Compute a delay between two {@link Date}.
     * This method give a way to translate from Java {@link Date} API into our Domain-specific language based on
     * {@link TimestampDelay}.
     *
     * @param expectedTime  the expectedTime time
     * @param effectiveTime the effective time
     * @return the number of milliseconds between the {@code expectedTime} and the {@code effectiveTime},
     * 0 if the {@code expectedTime} or the {@code expectedTime} is {@code null}.
     */
    public static Long computeDelay(Date expectedTime, Date effectiveTime) {
        Long result = 0L;

        if (expectedTime != null && effectiveTime != null) {
            LocalDateTime expected = TimestampDelay.toLocalDateTime(expectedTime);
            LocalDateTime effective = TimestampDelay.toLocalDateTime(effectiveTime);
            Duration duration = Duration.between(expected, effective);

            result = duration.toMillis();
        }

        return result;
    }
}