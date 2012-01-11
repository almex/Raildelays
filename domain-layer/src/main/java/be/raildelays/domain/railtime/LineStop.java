package be.raildelays.domain.railtime;

import java.io.Serializable;
import java.util.Date;

public class LineStop implements Serializable {

	private static final long serialVersionUID = 3019492480070457922L;
	
	private Station station;
	private Date from;
	private Date to;

	public LineStop (String stationName, Date from, Date to) {
		this.station = new Station(stationName);
		this.setFrom(from);
		this.setTo(to);
	}
	
	public Station getStation() {
		return station;
	}
	
	public void setStation(Station station) {
		this.station = station;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
