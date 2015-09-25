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
public class TimeBasedExcelRowComparator implements Comparator<ExcelRow> {


    @Override
    public int compare(ExcelRow lho, ExcelRow rho) {
        int result;

        if (lho == rho) {
            result = 0;
        } else if (lho == null) {
            result = -1;
        } else if (rho == null) {
            result = 1;
        } else {
            result = new CompareToBuilder()
                    .append(lho.getDate(), rho.getDate())
                    .append(lho.getExpectedDepartureTime(), rho.getExpectedDepartureTime())
                    .append(lho.getExpectedArrivalTime(), rho.getExpectedArrivalTime())
                    .build();
        }

        return result;
    }

    private static class CompareToBuilder {

        private int comparison = 0;

        /**
         * Adapter between {@link java.util.Comparator} and {@link java.lang.Comparable}.
         *
         * @param lho left hand object
         * @param rho right hand object
         * @param <T> the type of the elements to be compared
         * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater
         * than the specified object.
         */
        public static <T extends Comparable<T>> int compare(T lho, T rho) {
            return CompareToBuilder.nullsLast(new Comparator<T>() {
                @Override
                public int compare(T lho, T rho) {
                    return lho.compareTo(rho);
                }
            }).compare(lho, rho);
        }

        /**
         * <p>
         * Prior to JDK 8 we need this method to replace <code>static interface</code> method from
         * {@link java.util.Comparator}.
         * </p>
         * Returns a null-friendly comparator that considers {@code null} to be
         * less than non-null. When both are {@code null}, they are considered
         * equal. If both are non-null, the specified {@code Comparator} is used
         * to determine the order. If the specified comparator is {@code null},
         * then the returned comparator considers all non-null values to be equal.
         * <p>
         * <p>The returned comparator is serializable if the specified comparator
         * is serializable.
         *
         * @param <T>        the type of the elements to be compared
         * @param comparator a {@code Comparator} for comparing non-null values
         * @return a comparator that considers {@code null} to be less than
         * non-null, and compares non-null objects with the supplied
         * {@code Comparator}.
         */
        private static <T> Comparator<T> nullsLast(final Comparator<T> comparator) {
            return (lho, rho) -> {
                int result;

                if (lho == null) {
                    result = (rho == null) ? 0 : 1;
                } else if (rho == null) {
                    result = -1;
                } else {
                    result = comparator.compare(lho, rho);
                }

                return result;
            };
        }

        public CompareToBuilder append(Object lho, Object rho) {
            if (comparison == 0) {
                comparison = compare((Comparable) lho, (Comparable) rho);
            }

            return this;
        }

        public int build() {
            return comparison;
        }
    }
}
