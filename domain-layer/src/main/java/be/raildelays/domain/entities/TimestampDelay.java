package be.raildelays.domain.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * {@link Embeddable} entity that express an arrival or departure delay.
 * 
 * @author Almex
 */
@Embeddable
public class TimestampDelay implements Serializable, Cloneable {

	private static final long serialVersionUID = -1026179811764044178L;

	@Temporal(TemporalType.TIME)
	@NotNull
	protected Date expected;

	@NotNull
	protected Long delay; // in number of milliseconds

	/**
	 * Default constructor.
	 */
	public TimestampDelay() {
		this.expected = new Date();
		this.delay = 0L;
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param expected
	 *            time expected
	 * @param delay
	 *            delay in milliseconds
	 */
	public TimestampDelay(Date expected, Long delay) {
		this();
		setExpected(expected);
		setDelay(delay);
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
		} else {
			if (obj instanceof TimestampDelay) {
				TimestampDelay delay = (TimestampDelay) obj;

				result = new EqualsBuilder() //
						.append(expected, delay.getExpected()) //
						.append(delay, delay.getDelay()) //
						.isEquals();
			} else {
				result = false;
			}
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
	
	@Override
	public TimestampDelay clone() {		
		try {
			TimestampDelay timestampDelay = (TimestampDelay) super.clone();
			
			timestampDelay.expected = (Date) (expected != null ? expected.clone() : null);
	
			return timestampDelay;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("Parent class doesn't support clone", e);
		}
	}

	public final Date getExpected() {
		return (Date) (expected != null ? expected.clone() : null);
	}

	public final void setExpected(Date expected) {
		this.expected = (Date) (expected != null ? expected.clone() : null);
	}

	public final Long getDelay() {
		return delay;
	}

	public final void setDelay(Long delay) {
		this.delay = delay;
	}
}
