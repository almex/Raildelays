package be.raildelays.batch.support;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.scope.context.ChunkContext;
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
public abstract class ItemResourceLocator implements ResourceLocator, ChunkListener, ItemWriteListener<BatchExcelRow> {
    protected static final String FILENAME_SUFFIX_KEY = "resource.filename.suffix";

    protected static final String FILE_PATH_KEY = "resource.file.path";

    protected Resource resource;

    private ChunkContext context;

    @Override
    public void beforeChunk(ChunkContext context) {
        this.context = context;
        context.setAttribute(FILENAME_SUFFIX_KEY, null);
    }

    @Override
    public void afterChunk(ChunkContext context) {
        context.setAttribute(FILENAME_SUFFIX_KEY, null);
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }

    @Override
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        if (!items.isEmpty()) {
            // Retrieve first element of what would be written
            BatchExcelRow item = items.get(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String suffix = formatter.format(item.getDate());

            context.setAttribute(FILENAME_SUFFIX_KEY, suffix);
        }
    }

    @Override
    public void afterWrite(List<? extends BatchExcelRow> items) {

    }

    @Override
    public void onWriteError(Exception exception, List<? extends BatchExcelRow> items) {

    }


    protected File getFileBasedOnSuffix() throws IOException {
        String suffix = (String) context.getAttribute(FILENAME_SUFFIX_KEY);
        File result = resource.getFile(); // By default we return the resource itself

        if (suffix != null) {
            StringBuilder builder = new StringBuilder();
            File original = resource.getFile();
            String originalFileName = original.getName();

            int extensionIndex = originalFileName.lastIndexOf(".");
            builder.append(originalFileName.substring(0, extensionIndex));
            builder.append(" ");
            builder.append((String) context.getAttribute(FILENAME_SUFFIX_KEY));
            builder.append(originalFileName.substring(extensionIndex));

            result = new File(original.getParentFile(), builder.toString());
        }

        return result;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
