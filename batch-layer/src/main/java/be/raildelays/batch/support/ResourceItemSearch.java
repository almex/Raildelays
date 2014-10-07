package be.raildelays.batch.support;

import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public interface ResourceItemSearch<T extends Comparable<? super T>> {

    public static final int EOF = -1;

    int indexOf(T item, Resource resource) throws Exception;
}
