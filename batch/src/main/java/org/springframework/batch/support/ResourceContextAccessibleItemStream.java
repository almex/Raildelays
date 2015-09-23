package org.springframework.batch.support;

import org.springframework.batch.item.resource.ResourceAwareItemStream;
import org.springframework.batch.item.resource.ResourceContext;

/**
 * Simple interface to expose a {@link ResourceContext} via a getter.
 *
 * @author Almex
 */
public interface ResourceContextAccessibleItemStream extends ResourceAwareItemStream {

    ResourceContext getResourceContext();
}
