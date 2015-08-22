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

package be.raildelays.jpa;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

/**
 * {@link AttributeConverter} dedicated to deal conversion between {@link LocalTime} and {@link Time}.
 * This converter is part of JDK 8/JPA 2.1 compatibility workaround in the waiting of a new version JPA.
 * <p>
 * Note that we lose precision when a {@link LocalTime} is converted into a {@link Time} because the first one
 * record nano-seconds but the second one does not (i.e.: maximum precision is in seconds).
 *
 * @author Almex
 * @since 2.0
 */
@Converter(autoApply = true)
public class LocalTimeAttributeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime entityValue) {
        Time result = null;

        if (entityValue != null) {
            result = Time.valueOf(entityValue);
        }

        return result;
    }

    @Override
    public LocalTime convertToEntityAttribute(Time databaseValue) {
        LocalTime result = null;

        if (databaseValue != null) {
            result = databaseValue.toLocalTime();
        }

        return result;
    }
}
