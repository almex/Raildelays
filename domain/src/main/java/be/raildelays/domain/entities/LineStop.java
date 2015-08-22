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
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Line stop determine a stop for train line.
 * To help building this entity and as the only way to do it
 * we embedded a {@link be.raildelays.domain.entities.LineStop.Builder}.
 *
 * @author Almex
 * @see AbstractEntity
 * @since 1.0
 */
@Entity
@Table(name = "LINE_STOP", uniqueConstraints = @UniqueConstraint(columnNames = {
        "TRAIN_ID", "DATE", "STATION_ID"}, name ="LineStopUniqueBusinessKeyConstraint"))
public class LineStop extends AbstractEntity implements Comparable<LineStop> {

    private static final long serialVersionUID = 7142886242889314414L;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "TRAIN_ID")
    @NotNull
    protected Train train;

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
        this.canceledDeparture = false;
        this.canceledArrival = false;
        this.previous = null;
        this.next = null;
    }

    private LineStop(Builder builder) {
        this.id = builder.id;
        this.date = builder.date != null ? builder.date : null;
        this.train = builder.train;
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
                .append(date.format(DateTimeFormatter.ISO_DATE))
                .append(", ") //
                .append("train: {").append(train) //
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

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    // I want to expose the fact that this method does not throw CloneNotSupportedException
    @Override
    public LineStop clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            // Swallow the Exception because in that case it means that super does
            // not have to be involved in the cloning.
        }
        return new Builder(this).build();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param lineStop the line stop to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    @Override
    @SuppressWarnings("NullableProblems") // We handle it in our implemntation
    public int compareTo(final LineStop lineStop) {
        int result;

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

    public Long getId() {
        return id;
    }

    public TimeDelay getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(TimeDelay arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public TimeDelay getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(TimeDelay departureTime) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCanceled() {
        return canceledDeparture || canceledArrival;
    }

    /**
     * Use {@link #setCanceledDeparture} or {@link #setCanceledArrival} instead.
     */
    @Deprecated
    public void setCanceled(boolean canceled) {
        this.canceledDeparture = this.canceledArrival = canceled;
    }

    public boolean isCanceledDeparture() {
        return canceledDeparture;
    }

    public void setCanceledDeparture(boolean canceledDeparture) {
        this.canceledDeparture = canceledDeparture;
    }

    public boolean isCanceledArrival() {
        return canceledArrival;
    }

    public void setCanceledArrival(boolean canceledArrival) {
        this.canceledArrival = canceledArrival;
    }

    public LineStop getPrevious() {
        return previous;
    }

    public void setPrevious(LineStop previous) {
        if (previous != null) {
            previous.next = this;
        }
        this.previous = previous;
    }

    public LineStop getNext() {
        return next;
    }

    public void setNext(LineStop next) {
        if (next != null) {
            next.previous = this;
        }
        this.next = next;
    }

    /**
     * This builder is the only way to get a new instance of a {@link be.raildelays.domain.entities.LineStop}.
     *
     * @author Almex
     * @since 1.0
     */
    public static class Builder {
        private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        private Long id;
        private Train train;
        private Station station;
        private boolean canceledDeparture;
        private boolean canceledArrival;
        private LocalDate date;
        private TimeDelay arrivalTime;
        private TimeDelay departureTime;
        private Builder previous;
        private Builder next;

        public Builder() {
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
            this.canceledDeparture = lineStop.canceledDeparture;
            this.canceledArrival = lineStop.canceledArrival;

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
                        .train(nextLineStop.train)
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

        private static void validate(LineStop lineStop) throws IllegalArgumentException {
            Set<ConstraintViolation<LineStop>> violations = validator.validate(lineStop);
            if (!violations.isEmpty()) {
                StringBuilder builder = new StringBuilder();

                for (ConstraintViolation violation : violations) {
                    builder.append("\n");
                    builder.append(violation.getPropertyPath().toString());
                    builder.append(' ');
                    builder.append(violation.getMessage());
                }

                throw new IllegalArgumentException(builder.toString());
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

        /**
         * Use {@link #canceledDeparture} or {@link #canceledArrival} instead.
         */
        @Deprecated
        public Builder canceled(boolean canceled) {
            this.canceledDeparture = this.canceledArrival = canceled;

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
            LineStop result;

            result = new LineStop(this);
            validate(result);

            //-- Copy backward
            LineStop backwardLineStop = result;
            Builder previousBuilder = this.previous;
            while (previousBuilder != null) {
                backwardLineStop.previous = new LineStop(previousBuilder);
                backwardLineStop.previous.next = backwardLineStop;

                validate(backwardLineStop.previous);

                previousBuilder = previousBuilder.previous;
                backwardLineStop = backwardLineStop.previous;
            }

            //-- Copy forward
            LineStop forwardLineStop = result;
            Builder nextBuilder = this.next;
            while (nextBuilder != null) {
                forwardLineStop.next = new LineStop(nextBuilder);
                forwardLineStop.next.previous = forwardLineStop;

                validate(forwardLineStop.next);

                nextBuilder = nextBuilder.next;
                forwardLineStop = forwardLineStop.next;
            }

            return result;
        }

        @Override
        public String toString() {
            return build().toString();
        }
    }

}
