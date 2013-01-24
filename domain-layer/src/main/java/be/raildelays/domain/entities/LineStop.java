package be.raildelays.domain.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Line stop determine a stop for train line.
 * 
 * @author Almex
 */
@Entity
@Table(name = "LINE_STOP")
@NamedQueries({
// @NamedQuery(name="LineStop.findByStation",
// query="select o from LineStop o where o.train.railtimeId = :idTrain and o.departureTime.expected >= :departure and o.departureTime.expected <= :arrival"),
@NamedQuery(name = "LineStop.findByTrain", query = "select o from LineStop o where o.train.railtimeId = ?1 and o.departureTime.expected >= ?2 and o.departureTime.expected <= ?3") })
public class LineStop implements Serializable {

	private static final long serialVersionUID = 7142886242889314414L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "TRAIN_ID", nullable = false)
	private Train train;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_ID", nullable = false)
	private Station station;

	@Column(name = "CANCELED")
	private boolean canceled;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	private Date date;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_DELAY"), name = "delay") })
	private TimestampDelay arrivalTime;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_DELAY"), name = "delay") })
	private TimestampDelay departureTime;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional=true)
	@JoinColumn(name="PREVIOUS_ID")
	private LineStop previous;
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional=true)
	@JoinColumn(name="NEXT_ID")
	private LineStop next;

	private LineStop() {
		this.train = null;
		this.station = null;
		this.arrivalTime = null;
		this.departureTime = null;
		this.canceled = false;
		this.date = new Date();
	}

	public LineStop(Date date, Train train, Station station, TimestampDelay arrivalTime,
			TimestampDelay departureTime, boolean canceled) {
		this();
		this.date = date;
		this.train = train;
		this.station = station;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.canceled = canceled;
	}

	public LineStop(Date date, Train train, Station station, TimestampDelay arrivalTime,
			TimestampDelay departureTime) {
		this(date, train, station, arrivalTime, departureTime, false);
	}
	
	public LineStop(Date date, Train train, Station station) {
		this(date, train, station, null, null, false);
	}

	@Override
	public String toString() {
		return new StringBuilder("LineStop: ") //
				.append("{ ") //
				.append("id: " + id + ", ") //
				.append("date: ")
				.append(new SimpleDateFormat("dd/MM/yyyy").format(date)) 
				.append(", ") //
				.append("train: {").append(train) //
				.append("}, ") //
				.append("station: {").append(station) //
				.append("}, ") //
				.append("arrivalTime: {").append(arrivalTime).append("}, ") //
				.append("departureTime: {").append(departureTime).append("}, ") //
				.append("canceled: " + canceled + " ") //
				.append("} ").toString();
	}
	
	public String toStringUntilDeparture() {
		StringBuilder builder = new StringBuilder();
		
		if (getPrevious() != null) {
			builder.append(getPrevious().toStringUntilDeparture());
			builder.append(getPrevious().toString());
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	public String toStringUntilArrival() {
		StringBuilder builder = new StringBuilder();
		
		if (getNext() != null) {
			builder.append("\n");
			builder.append(getNext().toString());
			builder.append(getNext().toStringUntilArrival());
		}				
		
		return builder.toString();		
	}
	
	public String toStringAll() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(toStringUntilDeparture());
		builder.append("<<").append(toString()).append(">>");
		builder.append(toStringUntilArrival());
		
		return builder.toString();		
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof Station) {
				LineStop lineStop = (LineStop) obj;

				result = new EqualsBuilder() //
						.append(train, lineStop.getTrain()) //
						.append(station, lineStop.getStation()) //
						.append(date, lineStop.getDate()) //
						.isEquals();
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(11,3) //
				.append(train) //
				.append(station) //
				.append(date) //
				.toHashCode();
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public LineStop getPrevious() {
		return previous;
	}

	public void setPrevious(LineStop previous) {
		this.previous = previous;
	}

	public LineStop getNext() {
		return next;
	}

	public void setNext(LineStop next) {
		this.next = next;
	}

}
