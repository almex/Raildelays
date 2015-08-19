package be.raildelays.delays;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Value object that express an arrival or departure time plus its delay.
 *
 * @author Almex
 * @since 2.0
 */
public final class TimestampDelay implements Serializable, Comparable<TimestampDelay> {

    private static final long serialVersionUID = -1026179811764044178L;

    protected final Date expectedTime;

    protected final Long delay; // in number of milliseconds

    /**
     * Default constructor.
     * Build an {@code expectedTime} with current date and 0 delay.
     */
    private TimestampDelay() {
        this.expectedTime = new Date();
        this.delay = 0L;
    }

    /**
     * Initialization constructor.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @param delay        delay in milliseconds counting from the {@code expectedTime}
     */
    private TimestampDelay(Date expectedTime, Long delay) {
        this.expectedTime = (Date) expectedTime.clone();
        this.delay = delay;
    }

    /**
     * Create an an instance of {@link TimestampDelay} with the current {@link Date} for the expectedTime time and 0 delay.
     *
     * @return a non-null {@link TimestampDelay} with the current {@link Date} and 0 delay.
     */
    public static TimestampDelay now() {
        return new TimestampDelay();
    }

    /**
     * Create an an instance of {@link TimestampDelay} with the {@code expectedTime} and 0 delay.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @return a {@link TimestampDelay} with the {@code expectedTime} and 0 delay,
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimestampDelay of(Date expectedTime) {
        return expectedTime != null ? new TimestampDelay(expectedTime, 0L) : null;
    }

    /**
     * Create an an instance of {@link TimestampDelay} with the {@code expectedTime} and the {@code delay}.
     *
     * @param expectedTime the expected time such as : 1st January 2000 at 12:00
     * @param delay        delay in milliseconds counting from the {@code expectedTime}
     * @return a {@link TimestampDelay} with the {@code expectedTime} and the {@code delay},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimestampDelay of(Date expectedTime, Long delay) {
        return expectedTime != null ? new TimestampDelay(expectedTime, delay) : null;
    }

    /**
     * Create an an instance of {@link TimestampDelay} from two {@link Date} :
     * <ul>
     * <li>the expected time</li>
     * <li>the effective time</li>
     * </ul>
     *
     * @param expectedTime  the expected time of this instance of {@link TimestampDelay}
     * @param effectiveTime the effective time used to compute delay
     * @return a {@link TimestampDelay} with the computed delay between those two {@link Date},
     * {@code null} if the {@code expectedTime} is {@code null}.
     */
    public static TimestampDelay from(Date expectedTime, Date effectiveTime) {
        TimestampDelay result = null;

        if (expectedTime != null) {
            Long delay = UtilsDelay.computeDelay(expectedTime, effectiveTime);

            result = new TimestampDelay(expectedTime, delay);
        }

        return result;
    }


    /**
     * Create a clone of {@link TimestampDelay} for which we provide a new {@code delay}.
     *
     * @param timestampDelay we take only into account the {@code expectedTime} from this {@link TimestampDelay}
     * @param delay          delay in milliseconds counting from the {@code expectedTime}
     * @return a non-null {@link TimestampDelay} with the {@code expectedTime} and the {@code delay}.
     */
    public static TimestampDelay from(TimestampDelay timestampDelay, Long delay) {
        return new TimestampDelay(timestampDelay.getExpectedTime(), delay);
    }

    @Override
    public String toString() {
        return expectedTime != null ? expectedTime.toString() + " +" + delay + "ms" : "null";
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof TimestampDelay) {
            TimestampDelay target = (TimestampDelay) obj;

            //FIXME should use Date#equals(Date) instead
            if (this.expectedTime != null && this.delay != null) {
                result = this.expectedTime.equals(target.expectedTime) && this.delay.equals(target.delay);
            } else {
                if (this.expectedTime != null) {
                    result = this.expectedTime.equals(target.expectedTime) && target.delay == null;
                } else if (this.delay != null) {
                    result = target.expectedTime == null && this.delay.equals(target.delay);
                } else {
                    result = target.expectedTime == null && target.delay == null;
                }
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 1;

        hash = hash * 7 + (delay != null ? delay.hashCode() : 0);
        hash = hash * 3 + (expectedTime != null ? expectedTime.hashCode() : 0);

        return hash;
    }

    public final Date getExpectedTime() {
        return (Date) (expectedTime != null ? expectedTime.clone() : null);
    }

    public final Long getDelay() {
        return delay;
    }

    @Override
    public int compareTo(TimestampDelay target) {
        int result;

        if (target == null) {
            result = -1;
        } else {
            //FIXME should use Date#compareTo(Date) instead
            if (this.expectedTime != null && this.delay != null) {
                result = this.expectedTime.compareTo(target.expectedTime);

                if (result == 0) {
                    result = this.delay.compareTo(target.delay);
                }
            } else {
                if (this.expectedTime == null) {
                    result = (target.expectedTime == null ? 0 : 1);
                } else {
                    result = this.expectedTime.compareTo(target.expectedTime);

                    if (result == 0) {
                        result = (target.delay == null ? 0 : 1);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Compare if {@code this} {@link TimestampDelay} is after the {@code target} {@link TimestampDelay}.
     *
     * @param target the {@link TimestampDelay}  to compare with
     * @return {@code true} if {@code this} is after the{@code target}, {@code false} otherwise
     */
    public boolean after(TimestampDelay target) {
        return compareTo(target) > 0;
    }

    /**
     * Compare if {@code this} {@link TimestampDelay} is before the {@code target} {@link TimestampDelay}.
     *
     * @param target the {@link TimestampDelay}  to compare with
     * @return {@code true} if {@code this} is before the{@code target}, {@code false} otherwise
     */
    public boolean before(TimestampDelay target) {
        return compareTo(target) < 0;
    }

    /**
     * Compare if {@code this} {@link TimestampDelay} is after or equal to the {@code target} {@link TimestampDelay}.
     *
     * @param target the {@link TimestampDelay} to compare with
     * @return {@code true} if {@code this} is after or equal to the{@code target}, {@code false} otherwise
     */
    public boolean afterOrEqual(TimestampDelay target) {
        return compareTo(target) >= 0;
    }

    /**
     * Compare if {@code this} {@link TimestampDelay} is before or equal to the {@code target} {@link TimestampDelay}.
     *
     * @param target the {@link TimestampDelay} to compare with
     * @return {@code true} if {@code this} is before or equal to the{@code target}, {@code false} otherwise
     */
    public boolean beforeOrEqual(TimestampDelay target) {
        return compareTo(target) <= 0;
    }

    /**
     * Translate a {@link TimestampDelay} into a {@link Date}.
     *
     * @return a non-null {@link Date} in {@code ZoneOffset.UTC} which is a combination of {@code expectedTime}
     * plus its {@code delay}.
     */
    public Date toDate() {
        LocalDateTime result = LocalDateTime.from(expectedTime.toInstant().atZone(ZoneId.systemDefault()))
                .plus(delay, ChronoUnit.MILLIS);

        return Date.from(result.toInstant(ZoneOffset.UTC));
    }
}
