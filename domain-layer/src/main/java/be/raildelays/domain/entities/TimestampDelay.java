package be.raildelays.domain.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Embeddable;

/**
 * {@link Embeddable} entity that express an arrival or departure delay.
 * 
 * @author Almex
 */
@Embeddable
public class TimestampDelay implements Serializable, Cloneable {

	private static final long serialVersionUID = -1026179811764044178L;
	
	protected Timestamp expected;
	
	protected Long delay; // in number of milliseconds
	
	/**
	 * Default constructor.
	 */
	public TimestampDelay() {
		this.expected = new Timestamp(new Date().getTime());
		this.delay = 0L;
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param expected time expected
	 * @param delay delay in milliseconds
	 */
	public TimestampDelay(Timestamp expected, Long delay) {
		this.expected = expected;
		this.delay = delay;
	}
	
	public Timestamp getExpected() {
		return (Timestamp) expected.clone();
	}

	public void setExpected(Timestamp expected) {
		this.expected = expected;
	}
}
