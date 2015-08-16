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
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;

/**
 * @author Almex
 */
public class ExcelRowComparator implements Comparator<ExcelRow> {

    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        int result;

        if (lho == rho) {
            result = 0;
        } else if (lho == null) {
            result = rho == null ? 0 : -1;
        } else if (rho == null) {
            result = 1;
        } else {
            result = new CompareToBuilder()
                    .append(lho.getDate(), rho.getDate())
                    .append(lho.getArrivalStation(), rho.getArrivalStation())
                    .append(lho.getDepartureStation(), rho.getDepartureStation())
                    .append(lho.getLinkStation(), rho.getLinkStation())
                    .append(lho.getExpectedDepartureTime(), rho.getExpectedDepartureTime())
                    .append(lho.getExpectedArrivalTime(), rho.getExpectedArrivalTime())
                    .append(lho.getExpectedTrain1(), rho.getExpectedTrain1())
                    .append(lho.getExpectedTrain2(), rho.getExpectedTrain2())
                    .append(lho.getEffectiveDepartureTime(), rho.getEffectiveDepartureTime())
                    .append(lho.getEffectiveArrivalTime(), rho.getEffectiveArrivalTime())
                    .append(lho.getEffectiveTrain1(), rho.getEffectiveTrain1())
                    .append(lho.getEffectiveTrain2(), rho.getEffectiveTrain2())
                    .append(lho.getDelay(), rho.getDelay())
                    .toComparison();
        }

        return result;
    }
}
