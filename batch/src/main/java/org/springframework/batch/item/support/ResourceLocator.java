package org.springframework.batch.item.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ResourceLocator {

    Resource getResource(ExecutionContext context) throws IOException;

    void setResource(Resource resource);

}
