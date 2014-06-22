package be.raildelays.batch.support;

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;

/**
 * @author Almex
 */
public interface IndexedResourceAwareItemStreamReader<T> extends ResourceAwareItemReaderItemStream<T> {

    int getCurrentIndex();
}
