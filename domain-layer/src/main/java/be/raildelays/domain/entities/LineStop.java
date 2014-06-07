package be.raildelays.domain.entities;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Line stop determine a stop for train line.
 *
 * @author Almex *
 * @see Entity
 */
@Entity
@Table(name = "LINE_STOP", uniqueConstraints = @UniqueConstraint(columnNames = {
        "TRAIN_ID", "DATE", "STATION_ID"}))
public class LineStop implements Serializable, Cloneable, Comparable<LineStop> {

    private static final long serialVersionUID = 7142886242889314414L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    protected final Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TRAIN_ID")
    @NotNull
    protected Train train;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATION_ID")
    @NotNull
    protected Station station;

    @Column(name = "CANCELED")
    protected boolean canceled;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE")
    @NotNull
    protected Date date;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "ARRIVAL_TIME_EXPECTED"), name = "expected"),
            @AttributeOverride(column = @Column(name = "ARRIVAL_TIME_DELAY"), name = "delay")})
    protected TimestampDelay arrivalTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "DEPARTURE_TIME_EXPECTED"), name = "expected"),
            @AttributeOverride(column = @Column(name = "DEPARTURE_TIME_DELAY"), name = "delay")})
    protected TimestampDelay departureTime;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "PREVIOUS_ID")
    protected LineStop previous;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, optional = true)
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
        this.previous = null;
        this.next = null;
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
                .append("next: " + (next != null ? next.getId() : "N/A") + " ") //
                .append("previous: " + (previous != null ? previous.getId() : "N/A") + " ") //
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

    @Override
    public LineStop clone() {
        return new Builder(this).build();
    }

    @Override
    public int compareTo(LineStop lineStop) {
        int result = 0;

        if (lineStop == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(date, lineStop.getDate())
                    .append(station, lineStop.getStation())
                    .append(train, lineStop.getTrain())
                    .append(arrivalTime, lineStop.getArrivalTime())
                    .append(departureTime, lineStop.getDepartureTime())
                    .toComparison();
        }

        return result;
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

        private LineStop singleton;

        public Builder() {
            singleton = null;
        }

        public Builder(LineStop lineStop) {
            this(lineStop, true, true);
        }

        public Builder(LineStop lineStop, boolean copyPrevious, boolean copyNext) {
            this.id = lineStop.id;
            this.date = lineStop.date;
            this.train = lineStop.train;
            this.station = lineStop.station;
            this.arrivalTime = lineStop.arrivalTime;
            this.departureTime = lineStop.departureTime;
            this.canceled = lineStop.canceled;

            //-- Copy backward
            LineStop previousLineStop = lineStop.previous;
            Builder backwardBuilder = this;
            while (previousLineStop != null && copyPrevious) {
                backwardBuilder.previous = new Builder()
                        .id(previousLineStop.id)
                        .date(previousLineStop.date)
                        .train(previousLineStop.train)
                        .station(previousLineStop.station)
                        .arrivalTime(previousLineStop.arrivalTime)
                        .departureTime(previousLineStop.departureTime)
                        .canceled(previousLineStop.canceled)
                        .addNext(backwardBuilder);

                backwardBuilder = backwardBuilder.previous;
                previousLineStop = previousLineStop.previous;
            }

            //-- Copy forward
            LineStop nextLineStop = lineStop.next;
            Builder forwardBuilder = this;
            while (nextLineStop != null && copyNext) {
                forwardBuilder.next = new Builder()
                        .id(nextLineStop.id)
                        .date(nextLineStop.date)
                        .train(nextLineStop.train)
                        .station(nextLineStop.station)
                        .arrivalTime(nextLineStop.arrivalTime)
                        .departureTime(nextLineStop.departureTime)
                        .canceled(nextLineStop.canceled)
                        .addPrevious(forwardBuilder);

                forwardBuilder = forwardBuilder.next;
                nextLineStop = nextLineStop.next;
            }
        }


        public Builder id(Long id) {
            this.id = id;

            return this;
        }

        public Builder train(Train train) {
            this.train = train;

            return this;
        }

        public Builder station(Station station) {
            this.station = station;

            return this;
        }

        public Builder canceled(boolean canceled) {
            this.canceled = canceled;

            return this;
        }

        public Builder arrivalTime(TimestampDelay arrivalTime) {
            this.arrivalTime = arrivalTime;

            return this;
        }

        public Builder departureTime(TimestampDelay departureTime) {
            this.departureTime = departureTime;

            return this;
        }

        public Builder date(Date date) {
            this.date = date;

            return this;
        }

        public Builder addNext(Builder next) {
            if (next != null) {
                head(this, next);
            }

            return this;
        }

        public Builder addNext(LineStop next) {
            if (next != null) {
                head(this, new Builder(next));
            }

            return this;
        }

        public Builder getNext() {
            return next;
        }

        private static void head(Builder node, Builder head) {
            if (node.next == null) {
                node.next = head;
                head.previous = node;
                head.next = null;
            } else {
                head(node.next, head);
            }
        }

        public Builder addPrevious(Builder previous) {
            if (previous != null) {
                tail(this, previous);
            }

            return this;
        }

        public Builder addPrevious(LineStop previous) {
            if (previous != null) {
                tail(this, new Builder(previous));
            }

            return this;
        }

        public Builder getPrevious() {
            return previous;
        }

        private static void tail(Builder node, Builder tail) {
            if (node.previous == null) {
                node.previous = tail;
                tail.next = node;
                tail.previous = null;
            } else {
                tail(node.previous, tail);
            }
        }

        public LineStop build() {
            LineStop result = null;

            if (singleton == null) {
                result = singleton = new LineStop(this);

                //-- Copy backward
                LineStop backwardLineStop = result;
                Builder previousBuilder = this.previous;
                while (previousBuilder != null) {
                    backwardLineStop.previous = new LineStop(previousBuilder);
                    backwardLineStop.previous.next = backwardLineStop;

                    previousBuilder = previousBuilder.previous;
                    backwardLineStop = backwardLineStop.previous;
                }

                //-- Copy forward
                LineStop forwardLineStop = result;
                Builder nextBuilder = this.next;
                while (nextBuilder != null) {
                    forwardLineStop.next = new LineStop(nextBuilder);
                    forwardLineStop.next.previous = forwardLineStop;

                    nextBuilder = nextBuilder.next;
                    forwardLineStop = forwardLineStop.next;
                }
            } else {
                result = singleton;
            }

            return result;
        }

        @Override
        public String toString() {
            return build().toString();
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

    public LineStop getPrevious() {
        return previous;
    }

    public LineStop getNext() {
        return next;
    }

    public void setPrevious(LineStop previous) {
        if (previous != null) {
            previous.next = this;
        }
        this.previous = previous;
    }

    public void setNext(LineStop next) {
        if (next != null) {
            next.previous = this;
        }
        this.next = next;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setArrivalTime(TimestampDelay arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(TimestampDelay departureTime) {
        this.departureTime = departureTime;
    }

}
