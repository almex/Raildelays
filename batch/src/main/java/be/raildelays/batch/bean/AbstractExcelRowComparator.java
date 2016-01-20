package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Add commons methods to all {@link Comparator} capable of comparing {@link ExcelRow}s and all their sub-types.
 *
 * @author Almex
 * @since 2.0
 */
public abstract class AbstractExcelRowComparator<T extends ExcelRow> implements Comparator<T> {

    /**
     * Returns a {@link Comparator} which first checks if references are the same otherwise delegates comparison to the
     * specified {@link Comparator}.
     *
     * @param <T>        the type of the elements to be compared
     * @param comparator a {@code Comparator} for comparing non-equals references
     * @return a comparator that considers same references as equal.
     */
    public static <T extends Comparable<? extends T>> Comparator<T> compareReferences(Comparator<? super T> comparator) {
        return new ReferenceComparator<>(comparator);
    }

    /**
     * Optimized {@link Comparator} which checks first if references are the same and return 0 in that case otherwise
     * delegates the decision to another {@link Comparator}.
     *
     * @param <T> the type of the elements to be compared
     */
    final static class ReferenceComparator<T> implements Comparator<T>, Serializable {

        // if different reference otherwise Ts are considered equal
        private final Comparator<? super T> real;

        ReferenceComparator(Comparator<? super T> real) {
            this.real = real;
        }

        @Override
        public int compare(T lho, T rho) {
            if (lho == rho) {
                return 0;
            } else {
                return (real == null) ? 0 : real.compare(lho, rho);
            }
        }
    }
}
