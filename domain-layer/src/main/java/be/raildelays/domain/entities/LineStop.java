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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Line stop determine a stop for train line.
 * 
 * @author Almex * 
 * @see Entity
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
	private final Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "TRAIN_ID")
	@NotNull
	private final Train train;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_ID")
	@NotNull
	private final Station station;

	@Column(name = "CANCELED")
	private final boolean canceled;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	@NotNull
	private final Date date;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_DELAY"), name = "delay") })
	private final TimestampDelay arrivalTime;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_DELAY"), name = "delay") })
	private final TimestampDelay departureTime;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "PREVIOUS_ID")
	private final LineStop previous;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "NEXT_ID")
	private LineStop next;

	@SuppressWarnings("unused")
	private LineStop() {
		this.id = null;
		this.train = null;
		this.station = null;
		this.arrivalTime = null;
		this.departureTime = null;
		this.canceled = false;
		this.date = null;
		this.previous = null;
		this.next = null;
	}

	public LineStop(final Date date, final Train train, final Station station,
			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
			final boolean canceled, final LineStop previous) {
		this.id = null;
		this.train = train;
		this.station = station;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.canceled = canceled;
		this.date = (Date) (date != null ? date.clone() : null);
		if (previous != null) {
			previous.next = this;
		}
		this.previous = previous;
		this.next = null;
	}

	public LineStop(final Date date, final Train train, final Station station,
			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
			final boolean canceled) {
		this(date, train, station, arrivalTime, departureTime, canceled, null);
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
			if (obj instanceof LineStop) {
				LineStop lineStop = (LineStop) obj;

				result = new EqualsBuilder() //
						.append(train, lineStop.train) //
						.append(station, lineStop.station) //
						.append(date, lineStop.date) //
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

	public Long getId() {
		return id;
	}

	public TimestampDelay getArrivalTime() {
		return arrivalTime != null ? arrivalTime/*.clone()*/ : null;
	}

	public TimestampDelay getDepartureTime() {
		return departureTime != null ? departureTime/*.clone()*/ : null;
	}

	public Train getTrain() {
		return train;
	}

	public Station getStation() {
		return station;
	}

	public Date getDate() {
		return (Date) (date != null ? date.clone() : null);
	}

	public boolean isCanceled() {
		return canceled;
	}

	public LineStop getPrevious() {
		return previous;
	}

	public LineStop getNext() {
		return next;
	}

}
