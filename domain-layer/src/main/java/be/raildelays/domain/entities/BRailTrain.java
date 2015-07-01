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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Immutable entity defining a {@link Train} specific to BRail portal.
 *
 * @author Almex
 * @see Entity
 */
@Entity
public class BRailTrain extends Train {

    private static final long serialVersionUID = 7844213206211119783L;

    @Column(name = "BRAIL_ID", unique = true)
    private final String bRailId;

    @SuppressWarnings("unused")
    // Already implemented for a future usage
    private BRailTrain() {
        bRailId = "";
    }

    /**
     * @param name
     * @param bRailId
     */
    public BRailTrain(final String name, final String bRailId) {
        super(name);
        this.bRailId = bRailId;
    }

    @Override
    public String toString() {
        return new StringBuilder("BRailTrain: ") //
                .append("{ ") //
                .append("id: ").append(id).append(", ") //
                .append("railtimeId: ").append(bRailId).append(", ") //
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
            if (obj instanceof BRailTrain) {
                BRailTrain train = (BRailTrain) obj;

                result = new EqualsBuilder()
                        .append(bRailId, train.getbRailId()).isEquals();
            } else {
                result = false;
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder() //
                .append(bRailId) //
                .toHashCode();
    }

    public String getbRailId() {
        return bRailId;
    }

}
