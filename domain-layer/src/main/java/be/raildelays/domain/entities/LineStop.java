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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Line stop determine a stop for train line.
 * 
 * @author Alexis SOUMAGNE.
 */
@Entity
@Table(name = "LINE_STOP", uniqueConstraints = @UniqueConstraint(columnNames = {
		"TRAIN_ID", "DATE", "STATION_ID" }))
@NamedQueries({ @NamedQuery(name = "LineStop.findByTrain", query = "select o from LineStop o where o.train.railtimeId = ?1 and o.departureTime.expected >= ?2 and o.departureTime.expected <= ?3") })
public class LineStop implements Serializable, Cloneable {

	private static final long serialVersionUID = 7142886242889314414L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID")
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "TRAIN_ID")
	@NotNull
	private Train train;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_ID")
	@NotNull
	private Station station;

	@Column(name = "CANCELED")
	private boolean canceled;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	@NotNull
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

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "PREVIOUS_ID")
	private LineStop previous;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "NEXT_ID")
	private LineStop next;

	private LineStop() {
		this.id = null;
		this.train = null;
		this.station = null;
		this.arrivalTime = null;
		this.departureTime = null;
		this.canceled = false;
		this.date = new Date();
	}

	public LineStop(final Date date, final Train train, final Station station,
			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
			final boolean canceled, final LineStop previous) {
		this();
		this.train = train;
		this.station = station;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.canceled = canceled;
		this.date = date;
		if (previous != null) {
			previous.next = this;
		}
		this.previous = previous;
	}

	public LineStop(final Date date, final Train train, final Station station,
			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
			final boolean canceled) {
		this(date, train, station, arrivalTime, departureTime, canceled, null);
	}

	public LineStop(Date date, Train train, Station station,
			TimestampDelay arrivalTime, TimestampDelay departureTime) {
		this(date, train, station, arrivalTime, departureTime, false, null);
	}

	public LineStop(Date date, Train train, Station station, final LineStop previous) {
		this(date, train, station, null, null, false, previous);
	}

	public LineStop(Date date, Train train, Station station) {
		this(date, train, station, null, null, false, null);
	}

	@Override
	public String toString() {
		return new StringBuilder("LineStop: ")
				//
				.append("{ ")
				//
				.append("id: " + id + ", ")
				//
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

		if (previous != null) {
			builder.append(previous.toStringUntilDeparture());
			builder.append(previous.toString());
			builder.append("\n");
		}

		return builder.toString();
	}

	public String toStringUntilArrival() {
		StringBuilder builder = new StringBuilder();

		if (next != null) {
			builder.append("\n");
			builder.append(next.toString());
			builder.append(next.toStringUntilArrival());
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
		return new HashCodeBuilder(11, 3) //
				.append(train) //
				.append(station) //
				.append(date) //
				.toHashCode();
	}

	@Override
	public LineStop clone() {		
		try {
			LineStop result = (LineStop) super.clone();
			
			result.date = (Date) (date != null ? date.clone() : null);
			result.train = train != null ? train.clone() : null;
			result.station = station != null ? station.clone() : null;
			result.arrivalTime = arrivalTime != null ? arrivalTime.clone() : null;
			result.departureTime = departureTime != null ? departureTime.clone() : null;
	
			return result;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("Parent class doesn't support clone", e);
		}
	}

	public Long getId() {
		return id;
	}

	public TimestampDelay getArrivalTime() {
		return arrivalTime != null ? arrivalTime.clone() : null;
	}

	public void setArrivalTime(final TimestampDelay arrivalTime) {
		this.arrivalTime = arrivalTime != null ? arrivalTime.clone() : null;
	}

	public TimestampDelay getDepartureTime() {
		return departureTime != null ? departureTime.clone() : null;
	}

	public void setDepartureTime(final TimestampDelay departureTime) {
		this.departureTime = departureTime != null ? departureTime.clone() : null;
	}

	public Train getTrain() {
		return train != null ? train.clone() : null;
	}

	public void setTrain(final Train train) {
		this.train = train != null ? train.clone() : null;
	}

	public Station getStation() {
		return station != null ? station.clone() : null;
	}

	public void setStation(Station station) {
		this.station = station != null ? station.clone() : null;
	}

	public Date getDate() {
		return (Date) (date != null ? date.clone() : null);
	}

	public void setDate(final Date date) {
		this.date = (Date) (date != null ? date.clone() : null);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public LineStop getPrevious() {
		return previous != null ? previous.clone() : null;
	}

	public void setPrevious(final LineStop previous) {
		this.previous = previous != null ? previous.clone() : null;
	}

	public LineStop getNext() {
		return next != null ? next.clone() : null;
	}

	public void setNext(final LineStop next) {
		this.next = next != null ? next.clone() : null;
	}

}
