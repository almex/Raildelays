package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.Date;

public class Step extends LineStop implements Serializable  {

	private static final long serialVersionUID = -3386080893909407089L;

	private Integer delay;
	
	public Step (String stationName, Date from, Date to, Integer delay) {
		super(stationName, from, to);
		this.setDelay(delay);
	}

	public Integer getDelay() {
		return delay;
	}

	public void setDelay(Integer delay) {
		this.delay = delay;
	}
}
