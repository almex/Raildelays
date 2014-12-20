package be.raildelays.batch.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * We communicate through the {@link org.springframework.batch.item.ExecutionContext} the file name used to create a new file.
 * The actual resource is used to build the final of a new file.
 * <p>
 * MaxItemCount must be a dividend of chunk-size
 * <p>
 * original: path/filemane.extension
 * result: path/filename_suffix.extension
 *
 * @author Almex
 */
public class BySearchExcelResourceLocator extends AbstractItemResourceLocator {

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        Resource result = resource;

        if (context.containsKey(keyName)) {
            result = new FileSystemResource(new File(context.getString(keyName)));
        } else {
            File existingFile = getExistingFile();

            if (existingFile != null) {
                result = new FileSystemResource(existingFile);
            }
        }

        return result;
    }

}
