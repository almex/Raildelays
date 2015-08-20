package be.raildelays.delays;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * {@code TimeDelay} is an immutable object that represent a time plus its delay.
 * This is a {@code Value Object} class.
 *
 * @author Almex
 * @implSpec This class is immutable and thread-safe.
 * @since 2.0
 */
public final class TimeDelay implements Serializable, Comparable<TimeDelay> {

    private static final long DEFAULT_DELAY = 0L;
    private static final long serialVersionUID = -1026179811764044178L;
    private final LocalTime expectedTime;
    private final Long delay; // in number of milliseconds

    /**
     * Default constructor.
     * Build an {@code expectedTime} with current date and 0 delay.
     */
    private TimeDelay() {
        this.expectedTime = LocalTime.now();
        this.delay = DEFAULT_DELAY;
    }

    /**
     * Initialization constructor.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @param delay        delay in milliseconds counting from the {@code expectedTime}
     */
    private TimeDelay(LocalTime expectedTime, Long delay) {
        this.expectedTime = LocalTime.from(expectedTime);
        this.delay = delay != null ? delay : DEFAULT_DELAY;
    }

    /**
     * Create an an instance of {@link TimeDelay} with the current {@link Date} for the expectedTime time and 0 delay.
     *
     * @return a non-null {@link TimeDelay} with the current {@link Date} and 0 delay.
     */
    public static TimeDelay now() {
        return new TimeDelay();
    }

    /**
     * Create an an instance of {@link TimeDelay} with the {@code expectedTime} and 0 delay.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @return a {@link TimeDelay} with the {@code expectedTime} and 0 delay,
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimeDelay of(LocalTime expectedTime) {
        return expectedTime != null ? new TimeDelay(expectedTime, DEFAULT_DELAY) : null;
    }

    /**
     * Create an an instance of {@link TimeDelay} with the {@code expectedTime} and the {@code delay}.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @param delay        delay in milliseconds counting from the {@code expectedTime}
     * @return a {@link TimeDelay} with the {@code expectedTime} and the {@code delay},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimeDelay of(LocalTime expectedTime, Long delay) {
        return expectedTime != null ? new TimeDelay(expectedTime, delay) : null;
    }

    /**
     * Create an an instance of {@link TimeDelay} with the {@code expectedTime} given in parameter and 0 {@code delay}.
     *
     * @param expectedTime we take into account only the {@link #expectedTime} from this {@link TimeDelay}
     * @return a {@link TimeDelay} with the {@code expectedTime} and 0 {@code delay},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimeDelay of(TimeDelay expectedTime) {
        return expectedTime != null ? new TimeDelay(expectedTime.getExpectedTime(), DEFAULT_DELAY) : null;
    }

    /**
     * Create an an instance of {@link TimeDelay} from two {@link LocalTime} :
     * <ul>
     * <li>the expected time</li>
     * <li>the effective time</li>
     * </ul>
     *
     * @param expectedTime  the expected time of this instance of {@link TimeDelay}
     * @param effectiveTime the effective time used to compute delay
     * @return a {@link TimeDelay} with the computed delay between those two {@link Date},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimeDelay computeFrom(LocalTime expectedTime, LocalTime effectiveTime) {
        TimeDelay result = null;

        if (expectedTime != null) {
            Long delay = Delays.computeDelay(expectedTime, effectiveTime);

            result = new TimeDelay(expectedTime, delay);
        }

        return result;
    }

    /**
     * Returns a copy of the {@link TimeDelay} in parameter.
     *
     * @param timeDelay from which we copy the content
     * @return a non-null {@link TimeDelay} with the {@code expectedTime} and the {@code delay} of the argument.
     */
    public static TimeDelay from(TimeDelay timeDelay) {
        return new TimeDelay(timeDelay.getExpectedTime(), timeDelay.getDelay());
    }

    /**
     * Returns a copy of this {@link TimeDelay} for which we provide a new {@code delay}.
     *
     * @param delay delay in milliseconds counting from the {@code expectedTime}
     * @return a non-null {@link TimeDelay} with the {@code expectedTime} of this and the {@code delay}.
     */
    public TimeDelay withDelay(Long delay) {
        return new TimeDelay(this.getExpectedTime(), delay);
    }

    /**
     * Compare if {@code this} {@link TimeDelay} is after the {@code target} {@link TimeDelay}.
     *
     * @param target the {@link TimeDelay}  to compare with
     * @return {@code true} if {@code this} is after the{@code target}, {@code false} otherwise
     */
    public boolean after(TimeDelay target) {
        return compareTo(target) > 0;
    }

    /**
     * Compare if {@code this} {@link TimeDelay} is before the {@code target} {@link TimeDelay}.
     *
     * @param target the {@link TimeDelay}  to compare with
     * @return {@code true} if {@code this} is before the{@code target}, {@code false} otherwise
     */
    public boolean before(TimeDelay target) {
        return compareTo(target) < 0;
    }

    /**
     * Compare if {@code this} {@link TimeDelay} is after or equal to the {@code target} {@link TimeDelay}.
     *
     * @param target the {@link TimeDelay} to compare with
     * @return {@code true} if {@code this} is after or equal to the{@code target}, {@code false} otherwise
     */
    public boolean afterOrEqual(TimeDelay target) {
        return compareTo(target) >= 0;
    }

    /**
     * Compare if {@code this} {@link TimeDelay} is before or equal to the {@code target} {@link TimeDelay}.
     *
     * @param target the {@link TimeDelay} to compare with
     * @return {@code true} if {@code this} is before or equal to the{@code target}, {@code false} otherwise
     */
    public boolean beforeOrEqual(TimeDelay target) {
        return compareTo(target) <= 0;
    }

    /**
     * Translate a {@link TimeDelay} into a {@link LocalTime}.
     *
     * @return a non-null {@link LocalTime} which is a combination of {@code expectedTime}
     * plus its {@code delay}.
     */
    public LocalTime toLocalTime() {
        return expectedTime.plus(delay, ChronoUnit.MILLIS);
    }

    /**
     * Translate a {@link TimeDelay} into a {@link Date}.
     *
     * @return a non-null {@link Date} with {@link ZoneId#systemDefault()} which is a combination of {@code expectedTime}
     * plus its {@code delay}.
     */
    public Date toDate() {
        return Date.from(toLocalTime()
                        .atDate(LocalDate.now())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    @Override
    public String toString() {
        return expectedTime.toString() + " +" + delay + "ms";
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof TimeDelay) {
            TimeDelay target = (TimeDelay) obj;

            //FIXME should use Date#equals(Date) instead
            result = this.expectedTime.equals(target.expectedTime) && this.delay.equals(target.delay);
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 1;

        hash = hash * 7 + delay.hashCode();
        hash = hash * 3 + expectedTime.hashCode();

        return hash;
    }

    @Override
    public int compareTo(TimeDelay target) {
        int result;

        if (target == null) {
            result = -1;
        } else {
            //FIXME should use Date#compareTo(Date) instead
            result = this.expectedTime.compareTo(target.expectedTime);

            if (result == 0) {
                result = this.delay.compareTo(target.delay);
            }
        }

        return result;
    }

    public final LocalTime getExpectedTime() {
        return expectedTime;
    }

    public final long getDelay() {
        return delay;
    }
}
