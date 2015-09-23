package org.springframework.batch.item.resource;

import org.springframework.batch.item.ItemWriter;

/**
 * Due to an error in design this class should replace {@link org.springframework.batch.item.file.ResourceAwareItemWriterItemStream}
 *
 * @author Almex
 * @since 2.0
 */
public interface ResourceAwareItemStreamWriter<T> extends ItemWriter<T>, ResourceAwareItemStream {
}
