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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

import static java.util.Comparator.*;

/**
 * Entity defining a trainLine line.
 * To help building this entity and as the only way to do it
 * we embedded a {@link Builder}.
 * The unity of this entity is based on its {@link #routeId}
 *
 * @author Almex
 * @implNote this class apply the Value Object pattern and is therefor immutable
 * @see AbstractEntity
 */
@Entity
@Table(
        name = "TRAIN_LINE",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ROUTE_ID"}, name = "TrainLineUniqueBusinessKeyConstraint")
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class TrainLine extends AbstractEntity implements Comparable<TrainLine> {

    private static final long serialVersionUID = -1527666012499664304L;

    @Column(name = "SHORT_NAME")
    private String shortName;

    @Column(name = "LONG_NAME")
    private String longName;

    @Column(name = "ROUTE_ID")
    @NotNull
    private Long routeId;

    /**
     * Default constructor used by Hibernate.
     */
    protected TrainLine() {
    }

    protected TrainLine(Builder builder) {
        this.shortName = builder.shortName;
        this.longName = builder.longName;
        this.routeId = builder.routeId;
        this.id = builder.id;
    }

    @Override
    public String toString() {
        return new StringBuilder("TrainLine: ") //
                .append("{ ") //
                .append("id: ").append(id).append(", ") //
                .append("shortName: ").append(shortName).append(", ") //
                .append("longName: ").append(longName).append(", ") //
                .append("routeId: ").append(routeId) //
                .append(" }").toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof TrainLine) {
            result = compareTo((TrainLine) obj) == 0;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(routeId);
    }

    @Override
    public int compareTo(TrainLine trainLine) {
        return Objects.compare(this, trainLine, (lho, rho) ->
                comparing(TrainLine::getRouteId, nullsLast(naturalOrder()))
                        .compare(lho, rho)
        );
    }

    public static class Builder {
        private Long id;
        private String shortName;
        private String longName;
        private Long routeId;

        /**
         * Minimal initialization constructor.
         *
         * @param routeId id of this route (see GTFS documentation)
         */
        public Builder(final Long routeId) {
            this.routeId = routeId;
        }

        /**
         * Do a copy of the given {@link TrainLine}.
         *
         * @param toCopy {@link TrainLine} to copy
         */
        public Builder(final TrainLine toCopy) {
            if (toCopy != null) {
                this.routeId = toCopy.routeId;
                this.shortName = toCopy.shortName;
                this.longName = toCopy.longName;
            }
        }

        public Builder shortName(final String shortName) {
            this.shortName = shortName;
            return this;
        }

        public Builder longName(final String longName) {
            this.longName = longName;
            return this;
        }

        public Builder id(final Long id) {
            this.id = id;
            return this;
        }

        public TrainLine build() {
            return build(true);
        }

        public TrainLine build(final boolean validate) {
            TrainLine result = new TrainLine(this);

            if (validate) {
                validate(result);
            }

            return result;
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public Long getRouteId() {
        return routeId;
    }

    public String getName() {
        return "" + getRouteId();
    }
}
