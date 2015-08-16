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
 * Station specific to BRail portal.
 *
 * @author Almex
 * @see Entity
 */
@Entity
public class BRailStation extends Station {

    private static final long serialVersionUID = -5603009982357954186L;

    @Column(name = "BRAIL_ID", unique = true)
    private final String bRailId;

    @SuppressWarnings("unused")
    private BRailStation() {
        bRailId = "";
    }

    public BRailStation(final String name, final String bRailId) {
        super(name);
        this.bRailId = bRailId;
    }


    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else {
            if (obj instanceof BRailStation) {
                BRailStation station = (BRailStation) obj;

                result = new EqualsBuilder().append(bRailId,
                        station.getbRailId()).isEquals();
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

    @Override
    public String toString() {
        return new StringBuilder("BRailStation: ") //
                .append("{ ") //
                .append("id: ").append(getId()).append(", ") //
                .append("railtimeId: ").append(bRailId).append(", ") //
                .append("dutchName: ").append(getDutchName()).append(", ") //
                .append("englishName: ").append(getEnglishName()).append(", ") //
                .append("frenchName: ").append(getFrenchName()) //
                .append(" }").toString();
    }

    public String getbRailId() {
        return bRailId;
    }
}
