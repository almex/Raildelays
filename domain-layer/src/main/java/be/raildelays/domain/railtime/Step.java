package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.Date;

public class Step extends Stop implements Serializable  {

	private static final long serialVersionUID = -3386080893909407089L;

	private Integer delay;
	
	public Step (String stationName, Date hour, Integer delay) {
		super(stationName, hour);
		this.setDelay(delay);
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}
}
