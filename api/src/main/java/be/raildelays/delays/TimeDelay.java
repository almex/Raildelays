package be.raildelays.delays;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.util.Date;
import java.util.Objects;

/**
 * {@code TimeDelay} is an immutable object that represent a time plus its delay.
 * Out of it you can retrieve the {@code expectedTime} or the {@code effectiveTime},
 * where the {@code effectiveTime} is the {@code expectedTime} plus a {@code delay}.
 * This is a {@code Value Object} class.
 *
 * @author Almex
 * @implSpec This class is immutable and thread-safe.
 * @since 2.0
 */
public final class TimeDelay implements Serializable, Comparable<TimeDelay> {

    private static final long DEFAULT_DELAY = 0L;
    private static final long serialVersionUID = -1026179811764044178L;
    private static final ChronoUnit DELAY_UNIT = ChronoUnit.MILLIS;
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
     * Create an an instance of {@link TimeDelay} with the {@code expectedTime} and the {@code delay}
     * of a certain {@code unit}.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @param delay        delay in the {@code unit} counting from the {@code expectedTime}
     * @param unit         unit of the delay (unsupported units are {@code ChronoUnit.MICROS}, {@code ChronoUnit.NANOS}
     *                     and all not supported by {@link LocalTime#isSupported(TemporalUnit)})
     * @return a {@link TimeDelay} with the {@code expectedTime} and the {@code delay},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimeDelay of(LocalTime expectedTime, Long delay, TemporalUnit unit) {
        TimeDelay result = null;

        if (expectedTime != null) {
            Duration duration = Duration.of(DEFAULT_DELAY, DELAY_UNIT);

            if (delay != null && unit != null) {
                if (!unit.isSupportedBy(expectedTime) ||
                        ChronoUnit.NANOS.equals(unit) ||
                        ChronoUnit.MICROS.equals(unit)) {
                    throw new UnsupportedTemporalTypeException("This unit is not supported by a TimeDelay");
                }

                duration = Duration.of(delay, unit);
            }

            result = new TimeDelay(expectedTime, duration.toMillis());
        }

        return result;
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
    public boolean isAfter(TimeDelay target) {
        return compareTo(target) > 0;
    }

    /**
     * Compare if {@code this} {@link TimeDelay} is before the {@code target} {@link TimeDelay}.
     *
     * @param target the {@link TimeDelay}  to compare with
     * @return {@code true} if {@code this} is before the{@code target}, {@code false} otherwise
     */
    public boolean isBefore(TimeDelay target) {
        return compareTo(target) < 0;
    }

    /**
     * Translate a {@link TimeDelay} into a {@link LocalTime} in order to get the effective time of this
     * {@link TimeDelay}.
     *
     * @return a non-null {@link LocalTime} which is a combination of {@code expectedTime}
     * plus its {@code delay}.
     */
    public LocalTime getEffectiveTime() {
        return expectedTime.plus(delay, DELAY_UNIT);
    }

    /**
     * Translate a {@link TimeDelay} into a {@link LocalDateTime}.
     *
     * @param date the date to combien with, not null
     * @return a non-null {@link LocalDateTime} which is a combination of {@code expectedTime}
     * plus its {@code delay} and the {@code date}.
     */
    public LocalDateTime atDate(LocalDate date) {
        return getEffectiveTime().atDate(date);
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
        Objects.requireNonNull(target);

        return this.getEffectiveTime().compareTo(target.getEffectiveTime());
    }

    public final LocalTime getExpectedTime() {
        return expectedTime;
    }

    public final long getDelay() {
        return delay;
    }
}
