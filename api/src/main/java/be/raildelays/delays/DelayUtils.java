package be.raildelays.delays;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * This utility class make usage of JSR-310 from JDK 8 and allow comparison between {@link java.util.Date} and
 * {@link be.raildelays.delays.TimestampDelay}.
 *
 * @author Almex
 */
public final class DelayUtils {

    /**
     * Default constructor.
     */
    private DelayUtils() {
        // No instantiation is possible.
    }

    public static long compareTimeAndDelay(TimestampDelay departureB, Date departureA) {
        return -compareTimeAndDelay(departureA, departureB);
    }

    public static long compareTimeAndDelay(TimestampDelay departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA != null && departureB != null) {
            if (departureB.getExpected() != null && departureB.getExpected() != null) {
                LocalTime start = departureA.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();
                LocalTime end = departureB.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();

                if (departureA.getDelay() != null) {
                    end = end.plusMinutes(departureA.getDelay());
                }

                if (departureB.getDelay() != null) {
                    end = end.plusMinutes(departureB.getDelay());
                }

                Duration duration = Duration.between(start, end);

                result = -duration.toMillis(); // The result should be the opposite of a duration
            } else {
                if (departureA.getExpected() != null) {
                    result = 1;
                } else if (departureB.getExpected() != null) {
                    result = -1;
                }
            }
        } else {
            if (departureA != null) {
                result = 1;
            } else if (departureB != null) {
                result = -1;
            }
        }

        return result;
    }

    public static long compareTimeAndDelay(Date departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA != null && departureB != null) {
            if (departureB.getExpected() != null) {
                LocalTime start = departureA.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();
                LocalTime end = departureB.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();

                if (departureB.getDelay() != null) {
                    end = end.plusMinutes(departureB.getDelay());
                }

                Duration duration = Duration.between(start, end);

                result = -duration.toMillis(); // The result should be the opposite of a duration
            } else {
                result = 1;
            }
        } else {
            if (departureA != null) {
                result = 1;
            } else if (departureB != null) {
                result = -1;
            }
        }

        return result;
    }

    public static long compareTime(TimestampDelay departureB, Date departureA) {
        return -compareTime(departureA, departureB);
    }

    public static long compareTime(Date departureA, TimestampDelay departureB) {
        long result = 0;

        if (departureA != null && departureB != null) {
            if (departureB.getExpected() != null) {
                LocalTime start = departureA.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();
                LocalTime end = departureB.getExpected()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalTime();

                Duration duration = Duration.between(start, end);

                result = -duration.toMillis(); // The result should be the opposite of a duration
            } else {
                result = 1;
            }
        } else {
            if (departureA != null) {
                result = 1;
            } else if (departureB != null) {
                result = -1;
            }
        }

        return result;
    }
}