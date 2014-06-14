package be.raildelays.batch.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;

/**
 * @author Almex
 */
public class FileSystemResourceDecorator implements WritableResourceDecorator, InitializingBean {

    private WritableResource delegate;

    private Resource outputDirectory;

    public FileSystemResourceDecorator(String outputDirectory) {
        this.outputDirectory = new FileSystemResource(outputDirectory);
    }

    public FileSystemResourceDecorator(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(outputDirectory, "You should provide an output directory");
        Assert.isTrue(outputDirectory.getFile().isDirectory(), "The output directory must be directory and not a file");
    }


    @Override
    public boolean isWritable() {
        return delegate != null ? delegate.isWritable() : false;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return delegate != null ? delegate.getOutputStream() : null;
    }

    @Override
    public String getDescription() {
        return delegate != null ? delegate.getDescription() : null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return delegate != null ? delegate.getInputStream() : null;
    }

    @Override
    public boolean exists() {
        return delegate != null ? delegate.exists() : false;
    }

    @Override
    public boolean isReadable() {
        return delegate != null ? delegate.isReadable() : false;
    }

    @Override
    public boolean isOpen() {
        return delegate != null ? delegate.isOpen() : false;
    }

    @Override
    public URL getURL() throws IOException {
        return delegate != null ? delegate.getURL() : null;
    }

    @Override
    public URI getURI() throws IOException {
        return delegate != null ? delegate.getURI() : null;
    }

    @Override
    public File getFile() throws IOException {
        return delegate != null ? delegate.getFile() : null;
    }

    @Override
    public long contentLength() throws IOException {
        return delegate != null ? delegate.contentLength() : -1;
    }

    @Override
    public long lastModified() throws IOException {
        return delegate != null ? delegate.lastModified() : -1;
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        delegate = (WritableResource) outputDirectory.createRelative(relativePath);

        return this;
    }

    @Override
    public String getFilename() {
        return delegate != null ? delegate.getFilename() : null;
    }

    @Override
    public void setOutputDirectory(Resource outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public Resource getOutputDirectory() {
        return this.outputDirectory;
    }
}
