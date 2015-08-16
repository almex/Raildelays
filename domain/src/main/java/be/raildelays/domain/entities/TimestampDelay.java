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

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Immutable object that express an arrival or departure delay.
 *
 * @author Almex
 * @see Embeddable
 */
@Embeddable
public class TimestampDelay implements Serializable, Comparable<TimestampDelay> {

    private static final long serialVersionUID = -1026179811764044178L;

    @Temporal(TemporalType.TIME)
    protected final Date expected;

    protected final Long delay; // in number of milliseconds

    /**
     * Default constructor.
     */
    public TimestampDelay() {
        this.expected = null;
        this.delay = null;
    }

    /**
     * Initialization constructor.
     *
     * @param expected time expected
     * @param delay    delay in milliseconds
     */
    public TimestampDelay(final Date expected, final Long delay) {
        this.expected = (Date) (expected != null ? expected.clone() : null);
        this.delay = delay;
    }

    @Override
    public String toString() {
        return new StringBuilder("TimestampDelay: ") //
                .append("{ ") //
                .append("expected: ")
                .append(expected != null ? new SimpleDateFormat("HH:mm")
                        .format(expected) : null).append(", ") //
                .append("delay: ").append(delay) //
                .append(" }").toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof TimestampDelay) {
            TimestampDelay target = (TimestampDelay) obj;

            result = new EqualsBuilder() //
                    .append(this.expected, target.expected) //
                    .append(this.delay, target.delay) //
                    .isEquals();
        }

        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
                .append(expected) //
                .append(delay) //
                .toHashCode();
    }

    public final Date getExpected() {
        return (Date) (expected != null ? expected.clone() : null);
    }

    public final Long getDelay() {
        return delay;
    }

    @Override
    public int compareTo(TimestampDelay timestampDelay) {

        int result = 0;

        if (timestampDelay == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(expected, timestampDelay.getExpected())
                    .append(delay, timestampDelay.getDelay())
                    .toComparison();
        }

        return result;
    }
}
