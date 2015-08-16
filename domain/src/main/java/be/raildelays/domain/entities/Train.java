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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Entity defining a train.
 *
 * @author Almex
 * @see AbstractEntity
 */
@Entity
@Table(name = "TRAIN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Train extends AbstractI18nEntity {

    private static final long serialVersionUID = -1527666012499664304L;

    protected Train() {
        super();
    }

    public Train(String englishName) {
        super(englishName);
    }

    public Train(String name, Language language) {
        super(name, language);
    }

    public Train(String englishName, String dutchName, String frenchName) {
        super(englishName, dutchName, frenchName);
    }

    @Override
    public String toString() {
        return new StringBuilder("Train: ") //
                .append("{ ") //
                .append("id: ").append(id).append(", ") //
                .append("dutchName: ").append(dutchName).append(", ") //
                .append("englishName: ").append(englishName).append(", ") //
                .append("frenchName: ").append(frenchName) //
                .append(" }").toString();
    }
}
