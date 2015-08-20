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

package be.raildelays.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class simplifying parsing.
 *
 * @author Almex
 */
public final class ParsingUtil {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "hh:mm";
    public static final String TIMESTAMP_FORMAT = DATE_FORMAT + TIME_FORMAT;
    /**
     * Default constructor.
     */
    private ParsingUtil() {
        // No instantiation is possible.
    }

    public static LocalDateTime parseTimestamp(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
    }

    public static LocalTime parseTime(String value) {
        return LocalTime.parse(value, DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static LocalDate parseDate(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String formatTimestamp(LocalDateTime value) {
        return value.format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
    }

    public static String formatTime(LocalTime value) {
        return value.format(DateTimeFormatter.ofPattern(TIME_FORMAT));
    }

    public static String formatDate(LocalDate value) {
        return value.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }
}