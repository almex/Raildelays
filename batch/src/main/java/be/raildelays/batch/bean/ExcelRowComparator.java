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
public class ExcelRowComparator extends AbstractExcelRowComparator<ExcelRow> {

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        return nullsFirst(compareReferences(
                comparing(ExcelRow::getDate, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getArrivalStation, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getDepartureStation, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getLinkStation, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedDepartureTime, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedArrivalTime, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedTrain1, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedTrain2, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getEffectiveDepartureTime, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getEffectiveArrivalTime, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getEffectiveTrain1, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getEffectiveTrain2, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getDelay, nullsFirst(naturalOrder()))
        )).compare(lho, rho);
    }
}
