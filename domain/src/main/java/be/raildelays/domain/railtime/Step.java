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

package be.raildelays.domain.railtime;

import be.raildelays.delays.TimeDelay;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Step extends Stop implements Serializable, Comparable<Step> {

    private static final long serialVersionUID = -3386080893909407089L;

    private Long delay;

    private boolean canceled;

    private Integer ordinance;

    public Step(Integer ordinance, String stationName, LocalDateTime dateTime,
                Long delay, boolean canceled) {
        super(stationName, dateTime);
        this.ordinance = ordinance;
        this.delay = delay;
        this.canceled = canceled;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("step=[");

        builder.append(getStation());
        builder.append(", ");

        if (this.isCanceled()) {
            builder.append("canceled=true");
        } else {
            LocalTime effectiveTime = TimeDelay.of(getDateTime().toLocalTime(), delay).getEffectiveTime();

            builder.append("scheduledTime=");
            builder.append(getDateTime().toLocalTime().format(DateTimeFormatter.ISO_TIME));
            builder.append(", ");
            builder.append("effectiveTime=");
            builder.append(effectiveTime.format(DateTimeFormatter.ISO_TIME));
        }

        builder.append("]");

        return builder.toString();
    }

    public Integer getOrdinance() {
        return ordinance;
    }

    public void setOrdinance(Integer ordinance) {
        this.ordinance = ordinance;
    }

    @Override
    public int compareTo(Step step) {
        return new CompareToBuilder() //
                .append(this.getOrdinance(), step.getOrdinance()) //
                .toComparison();
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();

        hash = hash * 7 + (ordinance != null ? ordinance.hashCode() : 0);
        hash = hash * 13 + (delay != null ? delay.hashCode() : 0);
        hash = hash * 3 + (canceled ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = super.equals(object);

        if (result && object instanceof Step) {
            Step step = (Step) object;

            result = new EqualsBuilder()
                    .append(this.ordinance, step.ordinance)
                    .append(this.delay, step.delay)
                    .append(this.canceled, step.canceled)
                    .build();
        }

        return result;
    }

}
