package org.springframework.batch.item.support;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * This adapter implements the {@link org.springframework.core.io.Resource} interface and delegates every methods
 * to the resource retrieved via {@link org.springframework.batch.item.support.ResourceLocator}.
 * <p>
 *     This implementation uses the <code>Singleton</code> pattern to insure that we use at any time the same
 *     {@link org.springframework.core.io.Resource} between each calls.
 * </p>
 * <p>
 *     You must register this bean as a <code>StepListener</code> in order to be able to access
 *     the <code>Step</code> {@link org.springframework.batch.item.ExecutionContext}.
 * </p>
 *
 * @author Almex
 * @since 1.2
 * @see org.springframework.batch.core.StepListener
 */
public class ResourceLocatorAdapter implements Resource {

    private ResourceLocator resourceLocator;
    private Resource resource;
    private ExecutionContext executionContext;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public boolean exists() {
        return getResource().exists();
    }

    @Override
    public boolean isReadable() {
        return getResource().isReadable();
    }

    @Override
    public boolean isOpen() {
        return getResource().isOpen();
    }

    @Override
    public URL getURL() throws IOException {
        return getResource().getURL();
    }

    @Override
    public URI getURI() throws IOException {
        return getResource().getURI();
    }

    @Override
    public File getFile() throws IOException {
        return getResource().getFile();
    }

    @Override
    public long contentLength() throws IOException {
        return getResource().contentLength();
    }

    @Override
    public long lastModified() throws IOException {
        return getResource().lastModified();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        return getResource().createRelative(relativePath);
    }

    @Override
    public String getFilename() {
        return getResource().getFilename();
    }

    @Override
    public String getDescription() {
        return getResource().getDescription();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getResource().getInputStream();
    }

    private Resource getResource() {
        if (resource == null) {
            try {
                resource = resourceLocator.getResource(executionContext);
            } catch (IOException e) {
                throw new IllegalStateException("Error when retrieving resource from the resource locator", e);
            }
        }

        return resource;
    }

    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
}
