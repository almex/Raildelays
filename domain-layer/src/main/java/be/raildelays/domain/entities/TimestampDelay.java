package be.raildelays.domain.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * {@link Embeddable} entity that express an arrival or departure delay.
 * 
 * @author Almex
 */
@Embeddable
public class TimestampDelay implements Serializable, Cloneable {

	private static final long serialVersionUID = -1026179811764044178L;
	
	@Temporal(TemporalType.TIME)
	protected Date expected;
	
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
	 * @param expected time expected
	 * @param delay delay in milliseconds
	 */
	public TimestampDelay(Date expected, Long delay) {
		this.expected = expected;
		this.delay = delay;
	}
	
	public Date getExpected() {
		return (Date) expected.clone();
	}

	public void setExpected(Date expected) {
		this.expected = expected;
	}
}
