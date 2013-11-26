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
//@NamedQueries({ @NamedQuery(name = "LineStop.findByTrain", query = "select o from LineStop o where o.train.railtimeId = ?1 and o.departureTime.expected >= ?2 and o.departureTime.expected <= ?3") })
public class LineStop implements Serializable, Cloneable {

	private static final long serialVersionUID = 7142886242889314414L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID")
	protected final Long id;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "TRAIN_ID")
	@NotNull
	protected final Train train;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "STATION_ID")
	@NotNull
	protected final Station station;

	@Column(name = "CANCELED")
	protected final boolean canceled;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE")
	@NotNull
	protected final Date date;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "ARRIVAL_TIME_DELAY"), name = "delay") })
	protected final TimestampDelay arrivalTime;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_EXPECTED"), name = "expected"),
			@AttributeOverride(column = @Column(name = "DEPARTURE_TIME_DELAY"), name = "delay") })
	protected final TimestampDelay departureTime;

	@OneToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "PREVIOUS_ID")
	protected LineStop previous;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "NEXT_ID")
	protected LineStop next;
	
	protected LineStop() {
		this.id = null;
		this.date = null;
		this.train = null;
		this.station = null;
		this.arrivalTime = null;
		this.departureTime = null;
		this.canceled = false;
	}
	
	private LineStop(LineStop lineStop) {		
		this.id = lineStop.id;
		this.date = (Date) (lineStop.date != null ? lineStop.date.clone() : null);
		this.train = lineStop.train;
		this.station = lineStop.station;
		this.arrivalTime = lineStop.arrivalTime;
		this.departureTime = lineStop.departureTime;
		this.canceled = lineStop.canceled;
	}
	
	private LineStop(Builder builder) {		
		this.id = builder.id;
		this.date = (Date) (builder.date != null ? builder.date.clone() : null);
		this.train = builder.train;
		this.station = builder.station;
		this.arrivalTime = builder.arrivalTime;
		this.departureTime = builder.departureTime;
		this.canceled = builder.canceled;
	}

//	public LineStop(final Date date, final Train train, final Station station,
//			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
//			final boolean canceled, final LineStop previous) {
//		this.id = null;
//		this.train = train;
//		this.station = station;
//		this.arrivalTime = arrivalTime;
//		this.departureTime = departureTime;
//		this.canceled = canceled;
//		this.date = (Date) (date != null ? date.clone() : null);
//		if (previous != null) {
//			previous.next = this;
//		}
//		this.previous = previous;
//		this.next = null;
//	}
//
//	public LineStop(final Date date, final Train train, final Station station,
//			final TimestampDelay arrivalTime, final TimestampDelay departureTime,
//			final boolean canceled) {
//		this(date, train, station, arrivalTime, departureTime, canceled, null);
//	}

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
	
	public static class Builder {
		private Long id;
		private Train train;
		private Station station;
		private boolean canceled;
		private Date date;
		private TimestampDelay arrivalTime;
		private TimestampDelay departureTime;
		private Builder previous;
		private Builder next;
		
		public Builder() {
			
		}
		
		public Builder(LineStop lineStop) {
			this.id = lineStop.id;
			this.date = lineStop.date;
			this.train = lineStop.train;
			this.station = lineStop.station;
			this.arrivalTime = lineStop.arrivalTime;
			this.departureTime = lineStop.departureTime;
			this.canceled = lineStop.canceled;
			
			//-- Copy backward
			LineStop previousLineStop = lineStop.previous;
			Builder previousBuilder = this;
			while (previousLineStop != null) {
				previousBuilder.previous = new Builder()
								.id(previousLineStop.id)
								.date(previousLineStop.date)
								.train(previousLineStop.train)
								.station(previousLineStop.station)
								.arrivalTime(previousLineStop.arrivalTime)
								.departureTime(previousLineStop.departureTime)
								.canceled(previousLineStop.canceled)
								.addNext(previousBuilder);
				
				previousBuilder = previousBuilder.previous;
				previousLineStop = previousLineStop.previous;
			}
			
			//-- Copy forward
			LineStop nextLineStop = lineStop.next;
			Builder nextBuilder = this;
			while (nextLineStop != null) {
				nextBuilder.next = new Builder()
								.id(nextLineStop.id)
								.date(nextLineStop.date)
								.train(nextLineStop.train)
								.station(nextLineStop.station)
								.arrivalTime(nextLineStop.arrivalTime)
								.departureTime(nextLineStop.departureTime)
								.canceled(nextLineStop.canceled)
								.addPrevious(nextBuilder);
				
				nextBuilder = nextBuilder.next;
				nextLineStop = nextLineStop.next;
			}
		}
		
		
		public Builder id(Long id) {
			this.id = id;
			
			return this;
		}
		
		public Builder train(Train train ) {
			this.train = train;
			
			return this;
		}
		
		public Builder station(Station station ) {
			this.station = station;
			
			return this;
		}
		
		public Builder canceled(boolean canceled ) {
			this.canceled = canceled;
			
			return this;
		}
		
		public Builder arrivalTime(TimestampDelay arrivalTime ) {
			this.arrivalTime = arrivalTime;
			
			return this;
		}
		
		public Builder departureTime(TimestampDelay departureTime ) {
			this.departureTime = departureTime;
			
			return this;
		}
		
		public Builder date(Date date ) {
			this.date = date;
			
			return this;
		}
		
		public Builder addNext(Builder next) {
			if (next != null) {
				tail(this, next);
			}
			
			return this;
		}
		
		public Builder addNext(LineStop next) {
			if (next != null) {
				tail(this, new Builder(next));
			}
			
			return this;
		}
		
		public Builder getNext() {
			return next;
		}
		
		private static void tail(Builder node, Builder tail) {
			if (node.next == null) {
				node.next = tail;
				tail.previous = node;
			} else {
				tail(node.next, tail);
			}
		}
		
		public Builder addPrevious(Builder previous) {
			if  (previous != null) {
				head(this , previous);
			}
			
			return this;
		}
		
		public Builder addPrevious(LineStop previous) {
			if (previous != null) {
				head(this, new Builder(previous));
			}
			
			return this;
		}
		
		public Builder getPrevious() {
			return previous;
		}
		
		private static void head(Builder node, Builder head) {			
			if (node.previous == null) {
				node.previous = head;
				head.next = node;
			} else {
				head(node.previous, head);
			}
		}
		
		public LineStop build() {
			LineStop result = new LineStop(this);
			
			//-- Copy backward
			LineStop previousLineStop = result;
			Builder previousBuilder = this.previous;
			while (previousBuilder != null) {
				previousLineStop.previous = new LineStop(previousBuilder);
				previousLineStop.previous.next = previousLineStop;
				
				previousBuilder = previousBuilder.previous;
				previousLineStop = previousLineStop.previous;
			}
			
			//-- Copy forward
			LineStop nextLineStop = result;
			Builder nextBuilder = this.next;
			while (nextBuilder != null) {
				nextLineStop.next = new LineStop(nextBuilder);
				nextLineStop.next.previous = nextLineStop;
				
				nextBuilder = nextBuilder.next;
				nextLineStop = nextLineStop.next;
			}
			
			return result; 
		}
	}

	public Long getId() {
		return id;
	}

	public TimestampDelay getArrivalTime() {
		return arrivalTime;
	}

	public TimestampDelay getDepartureTime() {
		return departureTime;
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

	@Override
	protected LineStop clone() {
		LineStop result = new LineStop(this);
		
		if (this.next != null) {
			result.next = this.next.clone();
			result.next.previous = result;
		}
		
		if (this.previous != null) {
			result.previous = this.previous.clone();
			result.previous.next = result;
		}
		
		return result;
	}

	public LineStop getPrevious() {
		return previous;
	}

	public LineStop getNext() {
		return next;
	}

}
