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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Almex
 * @since 2.0
 */
public class LocalTimeAttributeConverterTest {

    private LocalTimeAttributeConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new LocalTimeAttributeConverter();
    }

    @Test
    public void testConvertToDatabaseColumn() throws Exception {
        Time databaseValue = Time.valueOf(LocalTime.now());
        LocalTime entityValue = databaseValue.toLocalTime();

        Assert.assertEquals(databaseValue, converter.convertToDatabaseColumn(entityValue));
    }

    @Test
    public void testConvertToEntityAttribute() throws Exception {
        LocalTime entityValue = LocalTime.now();
        Time databaseValue = Time.valueOf(entityValue);

        Assert.assertEquals(entityValue.truncatedTo(ChronoUnit.SECONDS), converter.convertToEntityAttribute(databaseValue));
    }

    @Test
    public void testRoundTrip1() throws Exception {
        LocalTime entityValue = LocalTime.now();

        Assert.assertEquals(entityValue.truncatedTo(ChronoUnit.SECONDS), converter.convertToEntityAttribute(converter.convertToDatabaseColumn(entityValue)));
    }

    @Test
    public void testRoundTrip2() throws Exception {
        Time databaseValue = Time.valueOf(LocalTime.now());

        Assert.assertEquals(databaseValue, converter.convertToDatabaseColumn(converter.convertToEntityAttribute(databaseValue)));
    }
}