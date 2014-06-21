package be.raildelays.batch.support;

import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public interface ItemSearch<T extends Comparable<? super T>> {

    int indexOf(T item, Resource resource) throws Exception;
}
