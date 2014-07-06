package be.raildelays.batch.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

/**
 * We communicate through the {@link org.springframework.batch.item.ExecutionContext} the file name used to create a new file.
 * The actual resource is used to build the final of a new file.
 *
 * MaxItemCount must be a dividend of chunk-size
 *
 * original: path/filemane.extension
 * result: path/filename_suffix.extension
 *
 * @author Almex
 */
public class ItemReaderResourceLocator extends ItemResourceLocator {
	
	@Override
	public Resource getResource(ExecutionContext context) throws IOException {
        Resource result = resource;
        String absolutePath = context.getString(FILE_PATH_KEY, resource.getFile().getParent());

        if (absolutePath != null) {
            result = new FileSystemResource(new File(absolutePath));
        }

        return result;
	}
	
}
