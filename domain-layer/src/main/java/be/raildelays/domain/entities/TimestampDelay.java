package be.raildelays.domain.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Immutable object that express an arrival or departure delay.
 * 
 * @author Almex
 * @see Embeddable
 */
@Embeddable
public class TimestampDelay implements Serializable, Comparable<TimestampDelay> {

	private static final long serialVersionUID = -1026179811764044178L;

	@Temporal(TemporalType.TIME)
	protected final Date expected;

	protected final Long delay; // in number of milliseconds

	/**
	 * Default constructor.
	 */
	public TimestampDelay() {
		this.expected = null;
		this.delay = null;
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param expected
	 *            time expected
	 * @param delay
	 *            delay in milliseconds
	 */
	public TimestampDelay(final Date expected, final Long delay) {
		this.expected = (Date) (expected != null ? expected.clone() : null);
		this.delay = delay;
	}

	@Override
	public String toString() {
		return new StringBuilder("TimestampDelay: ") //
				.append("{ ") //
				.append("expected: ")
				.append(expected != null ? new SimpleDateFormat("HH:mm")
						.format(expected) : null).append(", ") //
				.append("delay: ").append(delay) //
				.append(" }").toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
        } else if (obj instanceof TimestampDelay) {
            TimestampDelay target = (TimestampDelay) obj;

            result = new EqualsBuilder() //
                    .append(this.expected, target.expected) //
                    .append(this.delay, target.delay) //
                    .isEquals();
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(expected) //
				.append(delay) //
				.toHashCode();
	}

	public final Date getExpected() {
		return (Date) (expected != null ? expected.clone() : null);
	}

	public final Long getDelay() {
		return delay;
	}

    @Override
    public int compareTo(TimestampDelay timestampDelay) {
        return new CompareToBuilder()
                .append(expected, timestampDelay != null ? timestampDelay.getExpected() : null)
                .append(delay, timestampDelay != null ? timestampDelay.getDelay() : null)
                .toComparison();
    }
}
