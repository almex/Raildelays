package be.raildelays.batch.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.ResourceLocator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * This class is responsible to build path of a temporary {@link java.io.File} based on  the given
 * {@link org.springframework.core.io.Resource} parent directory and append the {@link #relativePathAndFileName} to it.
 * Then it stores, as a <code>String</code>, the resulting absolute path into the
 * {@link org.springframework.batch.item.ExecutionContext} using the {@link #keyName} .
 *
 * @author Almex
 * @since 1.2
 */
//TODO replace this implementation by using Files.createTempFile() and System.getProperty("java.io.tmpdir")
public class TemporaryFileResourceLocator implements ResourceLocator {

    private Resource resource;

    private String relativePathAndFileName;

    private String keyName;

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        Resource result = new FileSystemResource(resource.getFile().getParent() + File.separator
                + relativePathAndFileName);



        context.putString(keyName, result.getFile().getAbsolutePath());

        return result;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setRelativePathAndFileName(String relativePathAndFileName) {
        this.relativePathAndFileName = relativePathAndFileName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
