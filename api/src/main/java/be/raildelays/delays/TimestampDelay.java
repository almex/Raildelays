package be.raildelays.delays;

import java.io.Serializable;
import java.util.Date;

/**
 * Value object that express an arrival or departure time plus its delay.
 *
 * @author Almex
 * @since 2.0
 */
public class TimestampDelay implements Serializable, Comparable<TimestampDelay> {

    private static final long serialVersionUID = -1026179811764044178L;

    protected final Date expected;

    protected final Long delay; // in number of milliseconds

    /**
     * Default constructor.
     * Build an expected time with current date and 0 delay
     */
    public TimestampDelay() {
        this.expected = new Date();
        this.delay = 0L;
    }

    /**
     * Initialization constructor.
     *
     * @param expected time expected
     * @param delay    delay in milliseconds
     */
    public TimestampDelay(final Date expected, final Long delay) {
        this.expected = (Date) (expected != null ? expected.clone() : null);
        this.delay = delay;
    }

    /**
     * Initialization constructor with 0 delay.
     *
     * @param expected time expected
     */
    public TimestampDelay(final Date expected) {
        this.expected = (Date) (expected != null ? expected.clone() : null);
        this.delay = 0L;
    }

    @Override
    public String toString() {
        return expected != null ? expected.toString() + " +" + delay + "ms" : "null";
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof TimestampDelay) {
            TimestampDelay target = (TimestampDelay) obj;

            if (this.expected != null && this.delay != null) {
                result = this.expected.equals(target.expected) && this.delay.equals(target.delay);
            } else {
                if (this.expected != null) {
                    result = this.expected.equals(target.expected) && target.delay == null;
                } else if (this.delay != null) {
                    result = target.expected == null && this.delay.equals(target.delay);
                } else {
                    result = target.expected == null && target.delay == null;
                }
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        int hash = 1;

        hash = hash * 7 + (delay != null ? delay.hashCode() : 0);
        hash = hash * 3 + (expected != null ? expected.hashCode() : 0);

        return hash;
    }

    public final Date getExpected() {
        return (Date) (expected != null ? expected.clone() : null);
    }

    public final Long getDelay() {
        return delay;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") TimestampDelay target) {
        int result;

        if (target == null) {
            result = -1;
        } else {
            if (this.expected != null && this.delay != null) {
                result = this.expected.compareTo(target.expected);

                if (result == 0) {
                    result = this.delay.compareTo(target.delay);
                }
            } else {
                if (this.expected == null) {
                    result = (target.expected == null ? 0 : 1);
                } else {
                    result = this.expected.compareTo(target.expected);

                    if (result == 0) {
                        result = (target.delay == null ? 0 : 1);
                    }
                }
            }
        }

        return result;
    }
}
