package be.raildelays.batch.support;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

/**
 * @author Almex
 */
public interface IndexedResourceAwareItemStreamReader<T> extends ResourceAwareItemReaderItemStream<T> {

    /**
     * Give the index of the current item read.
     *
     * @return index of the current item or -1 if we have reach the end of file
     */
    int getCurrentIndex();
}
