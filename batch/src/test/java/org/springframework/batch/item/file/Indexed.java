package org.springframework.batch.item.file;

import org.springframework.batch.item.ItemCountAware;
import org.springframework.batch.item.ItemIndexAware;

/**
 * Purpose of this class is to have a simple implementation of {@link ItemIndexAware} for testing.
 *
 * @author Almex
 */
public class Indexed implements ItemIndexAware, ItemCountAware, Comparable<Indexed> {

    private String value;
    private Long index;

    public Indexed(String value) {
        this.value = value;
    }

    public Indexed(String value, Long index) {
        this.value = value;
        this.index = index;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    /**
     * index is zero-based but counting start at 1.
     */
    @Override
    public void setItemCount(int count) {
        setIndex((long) count - 1);
    }

    @Override
    public int compareTo(Indexed o) {
        int result = 1;

        if (o != null) {
            result = value.compareTo(o.toString());
        }

        return result;
    }
}
