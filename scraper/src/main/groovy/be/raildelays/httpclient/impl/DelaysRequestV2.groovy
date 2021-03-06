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

package be.raildelays.httpclient.impl

import be.raildelays.domain.Language
import be.raildelays.httpclient.AbstractRequest

import java.time.LocalDate

/**
 * @author Almex
 * @since 1.2
 */
public class DelaysRequestV2 extends AbstractRequest {

    public DelaysRequestV2(String trainId, LocalDate day, Language language) {
        setTrainId(trainId)
        setDay(day)
        setLanguage(language)
    }

    public String getTrainId() {
        return getValue("trainId");
    }

    private void setTrainId(String trainId) {
        setValue(trainId, "trainId", String.class);
    }

    public LocalDate getDay() {
        return getValue("day");
    }

    private void setDay(LocalDate day) {
        setValue(day, "day", Date.class);
    }

    public Language getLanguage() {
        return getValue("language");
    }

    private void setLanguage(Language language) {
        setValue(language, "language", Language.class);
    }

}
