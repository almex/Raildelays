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

import be.raildelays.domain.Language;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Immutable entity of a {@link Train} specific to Railtime portal.
 *
 * @author Almex
 * @see Train
 */
@Entity
public class RailtimeTrain extends Train {

    private static final long serialVersionUID = -7755979419472957633L;

    @Column(name = "RAILTIME_ID", unique = true)
    private final String railtimeId;

    @SuppressWarnings("unused")
    private RailtimeTrain() {
        super();
        this.railtimeId = "";
    }

    public RailtimeTrain(final String name, final String railtimeId) {
        super(name);
        this.railtimeId = railtimeId;
    }

    public RailtimeTrain(final String name, final String railtimeId, final Language language) {
        super(name, language);
        this.railtimeId = railtimeId;
    }

    @Override
    public String toString() {
        return new StringBuilder("RailtimeTrain: ") //
                .append("{ ") //
                .append("id: ").append(id).append(", ") //
                .append("railtimeId: ").append(railtimeId).append(", ") //
                .append("dutchName: ").append(dutchName).append(", ") //
                .append("englishName: ").append(englishName).append(", ") //
                .append("frenchName: ").append(frenchName) //
                .append(" }").toString();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else {
            if (obj instanceof RailtimeTrain) {
                RailtimeTrain train = (RailtimeTrain) obj;

                result = new EqualsBuilder().append(railtimeId,
                        train.getRailtimeId()).isEquals();
            } else {
                result = false;
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
                .append(railtimeId) //
                .toHashCode();
    }

    public String getRailtimeId() {
        return railtimeId;
    }

}
