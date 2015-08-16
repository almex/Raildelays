package org.springframework.batch.item.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Simply pass the delegated resource originally provided to this
 * {@link org.springframework.batch.item.support.ResourceLocator#setResource(org.springframework.core.io.Resource)}.
 *
 * @author Almex
 * @since 1.2
 */
public class PassThroughResourceLocator implements ResourceLocator{

    private Resource resource;

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
