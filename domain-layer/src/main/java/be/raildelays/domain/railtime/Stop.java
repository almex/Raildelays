package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.Date;

public class Stop implements Serializable {

	private static final long serialVersionUID = 3019492480070457922L;
	
	private Station station;
	private Date hour;

	public Stop (String stationName, Date hour) {
		this.station = new Station(stationName);
		this.hour = hour;
	}
	
	public Station getStation() {
		return station;
	}
	
	public void setStation(Station station) {
		this.station = station;
	}
	
	public Date getHour() {
		return hour;
	}
	
	public void setHour(Date hour) {
		this.hour = hour;
	}

}
