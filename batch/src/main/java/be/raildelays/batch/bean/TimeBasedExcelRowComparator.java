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

import static java.util.Comparator.*;

/**
 * It's the reverse of the chronological ordering but where <code>null</code> values must be last element in the list.
 * So, it's not the same as simply using {@code Collections.reverse()} as it would put
 * {@code null} values as first element in the list.
 * <p>
 * <p>
 * <p>
 * Example:
 * <ul>
 * <li>21/02/2013 null  null
 * <li>22/02/2013 08:00 09:00
 * <li>22/02/2013 16:00 17:00
 * <li>22/02/2013 null  null
 * <li>null
 * </ul>
 * </p>
 *
 * @author Almex
 * @see java.util.Collections
 * @since 1.2
 */
public class TimeBasedExcelRowComparator extends AbstractExcelRowComparator<ExcelRow> {

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        return nullsLast(compareReferences(
                comparing(ExcelRow::getDate, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedDepartureTime, nullsFirst(naturalOrder()))
                        .thenComparing(ExcelRow::getExpectedArrivalTime, nullsFirst(naturalOrder()))
                        .reversed()
        )).compare(lho, rho);
    }
}
