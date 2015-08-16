package org.springframework.batch.support;

import org.springframework.batch.item.ItemStream;
import org.springframework.core.io.Resource;

/**
 * Simple interface to expose a {@link Resource} via accessors.
 *
 * @author Almex
 */
public interface ResourceAwareItemStream extends ItemStream {

    Resource getResource();

    void setResource(Resource resource);
}
