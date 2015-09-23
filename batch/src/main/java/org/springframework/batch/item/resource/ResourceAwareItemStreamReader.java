package org.springframework.batch.item.resource;

import org.springframework.batch.item.ItemReader;

/**
 * Due to an error in design this class should replace {@link org.springframework.batch.item.file.ResourceAwareItemReaderItemStream}
 *
 * @author Almex
 * @since 2.0
 */
public interface ResourceAwareItemStreamReader<T> extends ResourceAwareItemStream, ItemReader<T> {
}
