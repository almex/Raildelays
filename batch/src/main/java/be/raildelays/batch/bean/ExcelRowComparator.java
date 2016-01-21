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

package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;

import java.util.Comparator;

import static java.util.Comparator.*;

/**
 * This {@link Comparator} compute the exact match between all values
 *
 * @author Almex
 * @since 1.1
 */
public class ExcelRowComparator<T extends ExcelRow> extends AbstractExcelRowComparator<ExcelRow<T>> {

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        return nullsLast(compareReferences(
                comparing(T::getDate, nullsLast(naturalOrder()))
                        .thenComparing(T::getArrivalStation, nullsLast(naturalOrder()))
                        .thenComparing(T::getDepartureStation, nullsLast(naturalOrder()))
                        .thenComparing(T::getLinkStation, nullsLast(naturalOrder()))
                        .thenComparing(T::getExpectedDepartureTime, nullsLast(naturalOrder()))
                        .thenComparing(T::getExpectedArrivalTime, nullsLast(naturalOrder()))
                        .thenComparing(T::getExpectedTrainLine1, nullsLast(naturalOrder()))
                        .thenComparing(T::getExpectedTrainLine2, nullsLast(naturalOrder()))
                        .thenComparing(T::getEffectiveDepartureTime, nullsLast(naturalOrder()))
                        .thenComparing(T::getEffectiveArrivalTime, nullsLast(naturalOrder()))
                        .thenComparing(T::getEffectiveTrainLine1, nullsLast(naturalOrder()))
                        .thenComparing(T::getEffectiveTrainLine2, nullsLast(naturalOrder()))
                        .thenComparing(T::getDelay, nullsLast(naturalOrder()))
        )).compare(lho, rho);
    }
}
