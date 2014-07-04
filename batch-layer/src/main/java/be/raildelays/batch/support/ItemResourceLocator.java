package be.raildelays.batch.support;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

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
public abstract class ItemResourceLocator implements ResourceLocator {

    protected Resource resource;

    public static final String FILE_PATH_KEY = "resource.file.path";


    protected File getFileBasedOnSuffix(ExecutionContext context) throws IOException {
        String suffix = context.getString(ResourceLocatorListener.FILENAME_SUFFIX_KEY);
        File result = resource.getFile(); // By default we return the resource itself

        if (suffix != null) {
            StringBuilder builder = new StringBuilder();
            File original = resource.getFile();
            String originalFileName = original.getName();

            int extensionIndex = originalFileName.lastIndexOf(".");
            builder.append(originalFileName.substring(0, extensionIndex));
            builder.append(" ");
            builder.append(suffix);
            builder.append(originalFileName.substring(extensionIndex));

            result = new File(original.getParentFile(), builder.toString());
        }

        return result;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
