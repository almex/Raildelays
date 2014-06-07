package be.raildelays.batch.support;

import org.springframework.batch.item.ItemStream;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public interface ResourceAwareItemStream extends ItemStream {

    Resource getResource();

    void setResource(Resource resource);
}
