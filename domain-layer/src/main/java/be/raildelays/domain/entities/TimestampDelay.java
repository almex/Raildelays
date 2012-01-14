package be.raildelays.domain.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Embeddable;

/**
 * {@link Embeddable} entity that express an arrival or departure delay.
 * 
 * @author Almex
 */
@Embeddable
public class TimestampDelay implements Serializable {

	private static final long serialVersionUID = -1026179811764044178L;
	
	protected Timestamp expected;
	
	protected Long delay; // in number of milliseconds

	public TimestampDelay(Timestamp expected, Long delay) {
		this.expected = expected;
		this.delay = delay;
	}
	
	public Timestamp getExpected() {
		return expected;
	}

	public void setExpected(Timestamp expected) {
		this.expected = expected;
	}
}
