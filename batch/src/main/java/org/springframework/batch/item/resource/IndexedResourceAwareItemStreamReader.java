package org.springframework.batch.item.resource;

/**
 * {@link ResourceAwareItemStreamReader} capable of returning the current index of the item read.
 *
 * @author Almex
 * @since 1.1
 */
public interface IndexedResourceAwareItemStreamReader<T> extends ResourceAwareItemStreamReader<T> {

    /**
     * Give the index of the current item read.
     *
     * @return index of the current item or -1 if we have reach the end of file
     */
    int getCurrentIndex();
}
