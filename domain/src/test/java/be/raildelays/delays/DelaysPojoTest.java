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

package be.raildelays.delays;

import be.raildelays.delays.TimeDelay;
import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.experimental.theories.DataPoint;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class DelaysPojoTest extends AbstractObjectTest {

    @DataPoint
    public static TimeDelay DATA_POINT1;
    @DataPoint
    public static TimeDelay DATA_POINT2;
    @DataPoint
    public static TimeDelay DATA_POINT3;
    @DataPoint
    public static TimeDelay DATA_POINT4;


    @Override
    public void setUp() throws Exception {
        DATA_POINT2 = TimeDelay.of(LocalTime.now());
        DATA_POINT3 = TimeDelay.of(LocalTime.MAX, -30L, ChronoUnit.MINUTES);
        DATA_POINT4 = TimeDelay.of(LocalTime.MIN, 30L, ChronoUnit.MINUTES);
    }
}