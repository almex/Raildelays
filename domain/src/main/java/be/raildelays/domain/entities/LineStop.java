/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.domain.entities;

import be.raildelays.delays.TimeDelay;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Line stop determine a stop for trainLine line.
 * To help building this entity and as the only way to do it
 * we embedded a {@link Builder}.
 *
 * @author Almex
 * @see AbstractEntity
 * @since 1.0
 * @implNote this class apply the Value Object pattern and is therefor immutable
 */
@Entity
@Table(
        name = "LINE_STOP",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"TRAIN_ID", "DATE", "STATION_ID"}, name = "LineStopUniqueBusinessKeyConstraint"
        )
)
public class LineStop extends AbstractEntity implements Comparable<LineStop> {

    private static final long serialVersionUID = 7142886242889314414L;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TRAIN_ID")
    @NotNull
    protected TrainLine trainLine;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "STATION_ID")
    @NotNull
    protected Station station;

    @Column(name = "CANCELED_DEPARTURE")
    protected boolean canceledDeparture;

    @Column(name = "CANCELED_ARRIVAL")
    protected boolean canceledArrival;

    @Column(name = "DATE")
    @NotNull
    protected LocalDate date;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "ARRIVAL_TIME_EXPECTED"), name = "expectedTime"),
            @AttributeOverride(column = @Column(name = "ARRIVAL_TIME_DELAY"), name = "delay")})
    protected TimeDelay arrivalTime;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(column = @Column(name = "DEPARTURE_TIME_EXPECTED"), name = "expectedTime"),
            @AttributeOverride(column = @Column(name = "DEPARTURE_TIME_DELAY"), name = "delay")})
    protected TimeDelay departureTime;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
    @PrimaryKeyJoinColumn(name = "PREVIOUS_ID")
    protected LineStop previous;

    @OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, optional = true, orphanRemoval = true)
    @PrimaryKeyJoinColumn(name = "NEXT_ID")
    protected LineStop next;

    /**
     * Default constructor used by Hibernate.
     */
    protected LineStop() {
        // Noop
    }

    private LineStop(Builder builder) {
        this.id = builder.id;
        this.date = builder.date != null ? builder.date : null;
        this.trainLine = builder.trainLine;
        this.station = builder.station;
        this.arrivalTime = builder.arrivalTime;
        this.departureTime = builder.departureTime;
        this.canceledDeparture = builder.canceledDeparture;
        this.canceledArrival = builder.canceledArrival;
    }

    @Override
    public String toString() {
        // I don't agree with that rule (it that case it's more efficient with a StringBuilder)
        //noinspection StringBufferReplaceableByString
        return new StringBuilder("LineStop: ") //
                .append("{ ") //
                .append("id: ").append(id).append(", ")  //
                .append("date: ")
                .append(date != null ? date.format(DateTimeFormatter.ISO_DATE) : "null")
                .append(", ") //
                .append("trainLine: {").append(trainLine) //
                .append("}, ") //
                .append("station: {").append(station) //
                .append("}, ") //
                .append("arrivalTime: {").append(arrivalTime).append("}, ") //
                .append("departureTime: {").append(departureTime).append("}, ") //
                .append("canceled: ").append(isCanceled()).append(" ") //
                .append("next: ").append(next != null ? next.getId() : "N/A").append(" ") //
                .append("previous: ").append(previous != null ? previous.getId() : "N/A").append(" ") //
                .append("} ").toString();
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
                        .append(trainLine, lineStop.trainLine) //
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
                .append(trainLine) //
                .append(station) //
                .append(date) //
                .toHashCode();
    }

    @Override
    @SuppressWarnings("NullableProblems") // We handle it in our implementation
    public int compareTo(final LineStop lineStop) {
        int result;

        if (lineStop == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(date, lineStop.getDate())
                    .append(station, lineStop.getStation())
                    .append(trainLine, lineStop.getTrainLine())
                    .append(arrivalTime, lineStop.getArrivalTime())
                    .append(departureTime, lineStop.getDepartureTime())
                    .toComparison();
        }

        return result;
    }

    @Override
    public Long getId() {
        return id;
    }

    public TimeDelay getArrivalTime() {
        return arrivalTime;
    }

    public TimeDelay getDepartureTime() {
        return departureTime;
    }

    public TrainLine getTrainLine() {
        return trainLine;
    }

    public Station getStation() {
        return station;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isCanceled() {
        return canceledDeparture || canceledArrival;
    }

    public boolean isCanceledDeparture() {
        return canceledDeparture;
    }

    public boolean isCanceledArrival() {
        return canceledArrival;
    }

    public LineStop getPrevious() {
        return previous;
    }

    public LineStop getNext() {
        return next;
    }

    /**
     * This builder is the only way to get a new instance of a {@link be.raildelays.domain.entities.LineStop}.
     *
     * @author Almex
     * @since 1.0
     */
    public static class Builder {

        private Long id;
        private TrainLine trainLine;
        private Station station;
        private boolean canceledDeparture;
        private boolean canceledArrival;
        private LocalDate date;
        private TimeDelay arrivalTime;
        private TimeDelay departureTime;
        private Builder previous;
        private Builder next;

        /**
         * Default constructor.
         */
        public Builder() {
            // Noop
        }

        /**
         * Copy constructor.
         *
         * @param lineStop to copy
         */
        public Builder(LineStop lineStop) {
            this(lineStop, true, true);
        }

        /**
         * Copy constructor where you can define if you want to copy linked elements.
         *
         * @param lineStop     to copy
         * @param copyPrevious enable copying of backwards links
         * @param copyNext     enable copying of forwards links
         */
        public Builder(LineStop lineStop, boolean copyPrevious, boolean copyNext) {
            if (lineStop != null) {
                this.id = lineStop.id;
                this.date = lineStop.date;
                this.trainLine = lineStop.trainLine;
                this.station = lineStop.station;
                this.arrivalTime = lineStop.arrivalTime;
                this.departureTime = lineStop.departureTime;
                this.canceledDeparture = lineStop.canceledDeparture;
                this.canceledArrival = lineStop.canceledArrival;

                //-- Copy backward
                LineStop previousLineStop = lineStop.previous;
                Builder backwardBuilder = this;
                while (previousLineStop != null && copyPrevious) {
                    backwardBuilder.previous = new Builder()
                            .id(previousLineStop.id)
                            .date(previousLineStop.date)
                            .trainLine(previousLineStop.trainLine)
                            .station(previousLineStop.station)
                            .arrivalTime(previousLineStop.arrivalTime)
                            .departureTime(previousLineStop.departureTime)
                            .canceledDeparture(previousLineStop.canceledDeparture)
                            .canceledArrival(previousLineStop.canceledArrival)
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
                            .trainLine(nextLineStop.trainLine)
                            .station(nextLineStop.station)
                            .arrivalTime(nextLineStop.arrivalTime)
                            .departureTime(nextLineStop.departureTime)
                            .canceledDeparture(nextLineStop.canceledDeparture)
                            .canceledArrival(nextLineStop.canceledArrival)
                            .addPrevious(forwardBuilder);

                    forwardBuilder = forwardBuilder.next;
                    nextLineStop = nextLineStop.next;
                }
            }
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

        private static void tail(Builder node, Builder tail) {
            if (node.previous == null) {
                node.previous = tail;
                tail.next = node;
                tail.previous = null;
            } else {
                tail(node.previous, tail);
            }
        }

        public Builder id(Long id) {
            this.id = id;

            return this;
        }

        public Builder trainLine(TrainLine trainLine) {
            this.trainLine = trainLine;

            return this;
        }

        public Builder station(Station station) {
            this.station = station;

            return this;
        }

        public Builder canceledDeparture(boolean canceled) {
            this.canceledDeparture = canceled;

            return this;
        }

        public Builder canceledArrival(boolean canceled) {
            this.canceledArrival = canceled;

            return this;
        }

        public Builder arrivalTime(TimeDelay arrivalTime) {
            this.arrivalTime = arrivalTime;

            return this;
        }

        public Builder departureTime(TimeDelay departureTime) {
            this.departureTime = departureTime;

            return this;
        }

        public Builder date(LocalDate date) {
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

        public LineStop build() {
            return build(true);
        }

        public LineStop build(final boolean validate) {
            LineStop result = doBuild();

            if (validate) {
                validate(result);
            }

            return result;
        }

        public LineStop doBuild() {
            LineStop result;

            result = new LineStop(this);

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

            return result;
        }
    }

}
