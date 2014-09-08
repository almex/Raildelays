package be.raildelays.delays;

import com.sun.istack.internal.Nullable;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
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
public final class DelayUtils {

    /**
     * Default constructor.
     */
    private DelayUtils() {
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
    public static long compareTimeAndDelay(@Nullable TimestampDelay timestampA, @Nullable TimestampDelay timestampB) {
        long result = 0;

        if (timestampA != null && timestampB != null) {
            if (timestampA.getExpected() != null && timestampB.getExpected() != null) {
                LocalTime start = timestampA.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();
                LocalTime end = timestampB.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();

                if (timestampA.getDelay() != null) {
                    start = start.plusMinutes(timestampA.getDelay());
                }

                if (timestampB.getDelay() != null) {
                    end = end.plusMinutes(timestampB.getDelay());
                }

                Duration duration = Duration.between(start, end);

                result = -duration.toMillis(); // The result should be the opposite of a duration
            } else if (timestampA.getExpected() != null) {
                result = 1;
            } else if (timestampB.getExpected() != null) {
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
    public static long compareTimeAndDelay(@Nullable Date date, @Nullable TimestampDelay timestamp) {
        return compareTimeAndDelay(new TimestampDelay(date), timestamp);
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * (taking into account delay only from the <code>timestamp</code>).
     *
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @param date      a {@link java.util.Date}
     * @return number of milliseconds between <code>timestamp</code> and <code>date</code>
     */
    public static long compareTimeAndDelay(@Nullable TimestampDelay timestamp, @Nullable Date date) {
        return compareTimeAndDelay(timestamp, new TimestampDelay(date));
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delays of both parameters.
     *
     * @param timestampA first {@link be.raildelays.delays.TimestampDelay}
     * @param timestampB second {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>timestampA</code> and <code>timestampB</code>
     */
    public static long compareTime(@Nullable TimestampDelay timestampA, @Nullable TimestampDelay timestampB) {
        return compareTimeAndDelay(timestampA != null ? new TimestampDelay(timestampA.getExpected()) : null, timestampB != null ? new TimestampDelay(timestampB.getExpected()) : null);
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delay from <code>timestamp</code>.
     *
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @param date      a {@link java.util.Date}
     * @return number of milliseconds between <code>timestamp</code> and <code>date</code>
     */
    public static long compareTime(@Nullable TimestampDelay timestamp, @Nullable Date date) {
        return compareTimeAndDelay(timestamp != null ? new TimestampDelay(timestamp.getExpected()) : null, new TimestampDelay(date));
    }

    /**
     * Compare two {@link be.raildelays.delays.TimestampDelay} and compute duration between those two
     * without taking into account delay from <code>timestamp</code>.
     *
     * @param date      a {@link java.util.Date}
     * @param timestamp a {@link be.raildelays.delays.TimestampDelay}
     * @return number of milliseconds between <code>date</code> and <code>timestamp</code>
     */
    public static long compareTime(@Nullable Date date, @Nullable TimestampDelay timestamp) {
        return compareTimeAndDelay(new TimestampDelay(date), timestamp != null ? new TimestampDelay(timestamp.getExpected()) : null);
    }
}