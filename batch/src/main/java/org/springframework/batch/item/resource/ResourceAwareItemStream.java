package org.springframework.batch.item.resource;

import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ResourceAware;

/**
 * Combination of {@link ItemStream} and {@link ItemStream} interfaces.
 *
 * @author Almex
 * @since 2.0
 */
public interface ResourceAwareItemStream extends ItemStream, ResourceAware {
}
