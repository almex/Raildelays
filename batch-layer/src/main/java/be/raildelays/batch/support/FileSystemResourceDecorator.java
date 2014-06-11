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

/**
 * @author Almex
 */
public class FileSystemResourceDecorator extends AbstractResource implements WritableResourceDecorator, InitializingBean {

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
        return delegate.isWritable();
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
    public File getFile() throws IOException {
        return delegate != null ? delegate.getFile() : null;
    }

    @Override
    public Resource createNewResource(String fileName) throws Exception {
        delegate = (WritableResource) outputDirectory.createRelative(fileName);

        return delegate;
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
