package be.raildelays.batch.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * We communicate through the {@link ExecutionContext} the file name used to create a new file.
 * The actual resource is used to build the final of a new file.
 * <p>
 * MaxItemCount must be a dividend of chunk-size
 * <p>
 * original: path/filemane.extension
 * result: path/filename_suffix.extension
 *
 * @author Almex
 */
public class ToWriteExcelResourceLocator extends AbstractItemResourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ToWriteExcelResourceLocator.class);

    private boolean forceNewFile = false;

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        File file = null;

        if (!forceNewFile) {
            file = getExistingFile();
        }

        if (file == null) {
            file = getFileBasedOnSuffix(context);
        }

        context.putString(keyName, file.getAbsolutePath());

        return new FileSystemResource(file);
    }

    public void setForceNewFile(boolean forceNewFile) {
        this.forceNewFile = forceNewFile;
    }
}
