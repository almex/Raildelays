package be.raildelays.batch.support;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
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
public abstract class AbstractItemResourceLocator implements ResourceLocator {

    public static final String FILE_PATH_KEY = "resource.file.path";
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractItemResourceLocator.class);
    protected Resource resource;
    private ResourceItemSearch<BatchExcelRow> resourceItemSearch;

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

    public File getExistingFile() throws IOException {
        File directory = resource.getFile().isDirectory() ? resource.getFile() : resource.getFile().getParentFile();
        File result = null;

        if (directory != null) {
            try {
                for (File file : directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
                    }
                })) {
                    try {
                        int currentRowIndex = resourceItemSearch.indexOf(new BatchExcelRow.Builder(null, null).delay(0L).build(), new FileSystemResource(file));

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
        }

        return result;
    }

    public void setResourceItemSearch(ResourceItemSearch<BatchExcelRow> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }
}
