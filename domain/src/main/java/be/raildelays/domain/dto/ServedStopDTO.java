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

package be.raildelays.domain.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

public final class ServedStopDTO implements Serializable, Cloneable {

    private static final long serialVersionUID = 3019492480070457922L;

    @NotNull
    private final String stationName;

    @NotNull
    private final LocalTime arrivalTime;

    @NotNull
    private final LocalTime departureTime;

    private final long arrivalDelay;

    private final long departureDelay;

    private final boolean canceled;

    public ServedStopDTO(final String stationName,
                         final LocalTime departure,
                         final long departureDelay,
                         final LocalTime arrival,
                         final long arrivalDelay,
                         final boolean canceled) {
        this.stationName = stationName;
        this.canceled = canceled;
        this.departureTime = departure;
        this.departureDelay = departureDelay;
        this.arrivalTime = arrival;
        this.arrivalDelay = arrivalDelay;
    }

    @Override
    public String toString() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");

        return new StringBuilder("ServedStopDTO: ") //
                .append("{ ") //
                .append("stationName: " + stationName) //
                .append(", ") //
                .append("arrivalTime: ")
                .append(arrivalTime != null ? df.format(arrivalTime) : "N/A") //
                .append(", ") //
                .append("departureTime: ")
                .append(departureTime != null ? df.format(departureTime) : "N/A") //
                .append(", ") //
                .append("arrivalDelay: " + arrivalDelay) //
                .append(", ") //
                .append("departureDelay: " + departureDelay) //
                .append(", ") //
                .append("canceled: " + canceled) //
                .append("} ").toString();
    }

    @Override
    public ServedStopDTO clone() {
        try {
            return (ServedStopDTO) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("The parent class is not cloneable", e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else {
            if (obj instanceof ServedStopDTO) {
                ServedStopDTO stop = (ServedStopDTO) obj;

                result = new EqualsBuilder() //
                        .append(stationName, stop.getStationName())
                        .isEquals();
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(11, 3) //
                .append(stationName)
                .toHashCode();
    }

    public String getStationName() {
        return stationName;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public long getArrivalDelay() {
        return arrivalDelay;
    }

    public long getDepartureDelay() {
        return departureDelay;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
