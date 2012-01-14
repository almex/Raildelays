package be.raildelays.domain.entities;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Line stop determine a stop for train line.
 * 
 * @author Almex
 */
@Entity
@Table(name="LINE_STOP")
public class LineStop implements Serializable {

	private static final long serialVersionUID = 7142886242889314414L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private Train train;
	
	private Station station;
	
	private boolean canceled;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column=@Column(name="ARRIVAL_TIME_EXPECTED"), name = "expected"),
		@AttributeOverride(column=@Column(name="ARRIVAL_TIME_DELAY"), name = "delay")
	})
	private TimestampDelay arrivalTime;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(column=@Column(name="DEPARTURE_TIME_EXPECTED"), name = "expected"),
		@AttributeOverride(column=@Column(name="DEPARTURE_TIME_DELAY"), name = "delay")
	})
	private TimestampDelay departureTime;
	
	public LineStop(Train train, Station station, TimestampDelay arrivalTime, TimestampDelay departureTime, boolean canceled) {
		this.train = train;
		this.station = station;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
	}
	
	public LineStop(Train train, Station station, TimestampDelay arrivalTime, TimestampDelay departureTime) {
		this(train, station, arrivalTime, departureTime, false);
	}

	public Long getId() {
		return id;
	}

	public TimestampDelay getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(TimestampDelay arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public TimestampDelay getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(TimestampDelay departureTime) {
		this.departureTime = departureTime;
	}

	public Train getTrain() {
		return train;
	}

	public void setTrain(Train train) {
		this.train = train;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

}
