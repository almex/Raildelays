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
public class ItemWriterResourceLocator extends ItemResourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemWriterResourceLocator.class);

    private ResourceItemSearch<BatchExcelRow> resourceItemSearch;
	
	@Override
	public Resource getResource(ExecutionContext context) throws IOException {
        File file = getExistingFile();

        if (file == null) {
            file = getFileBasedOnSuffix(context);
        }

        context.putString(FILE_PATH_KEY, file.getAbsolutePath());

        return new FileSystemResource(file);
	}

    public File getExistingFile() throws IOException {
        File directory = resource.getFile().getParentFile();
        File result = null;

        try {
            for (File file : directory.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
                }
            })) {
                try {

                    int currentRowIndex = resourceItemSearch.indexOf(new BatchExcelRow.Builder(null, null).build(), new FileSystemResource(file));

                    if (currentRowIndex != -1) {
                        result = file;
                    }
                } catch (InvalidFormatException e) {
                    LOGGER.error("Excel format not supported for this workbook!", e);
                } catch (IOException e) {
                    LOGGER.error("Error when opening an Excel workbook", e);
                }
            }
        } catch (Exception e) {
            throw new IOException("Cannot find content in your Excel file", e);
        }

        return result;
    }

    public void setResourceItemSearch(ResourceItemSearch<BatchExcelRow> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }
}
