package be.raildelays.batch.support;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.Format;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * We communicate through the {@link ExecutionContext} the file name used to create a new file.
 * The actual resource is used to build the final of a new file.
 *
 * MaxItemCount must be a dividend of chunk-size
 *
 * original: path/filemane.extension
 * result: path/filename_suffix.extension
 *
 * @author Almex
 */
public class ItemWriterResourceLocator extends AbstractItemResourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemWriterResourceLocator.class);

    @Override
    public Resource getResource(ExecutionContext context) throws IOException {
        File file = getExistingFile();

        if (file == null) {
            file = getFileBasedOnSuffix(context);
        }

        context.putString(FILE_PATH_KEY, file.getAbsolutePath());

        return new FileSystemResource(file);
    }

}
