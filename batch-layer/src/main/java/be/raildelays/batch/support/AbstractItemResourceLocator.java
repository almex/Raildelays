package be.raildelays.batch.support;

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.ResourceLocator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static org.springframework.batch.item.file.ExcelSheetItemWriter.Format;

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
 * @see ResourceLocatorListener
 */
public abstract class AbstractItemResourceLocator implements ResourceLocator {

    public String keyName;
    protected Resource resource;
    private ResourceItemSearch<BatchExcelRow> resourceItemSearch;

    protected File getFileBasedOnSuffix(ExecutionContext context) throws IOException {
        String suffix = context.getString(ResourceLocatorListener.FILENAME_SUFFIX_KEY);
        File result = resource.getFile(); // By default we return the resource itself

        if (suffix != null) {
            String fileName = ExcelFileUtils.getFileName(resource.getFile());
            String fileExtension = ExcelFileUtils.getFileExtension(resource.getFile());

            result = ExcelFileUtils.getFile(resource.getFile().getParentFile(), fileName, suffix, fileExtension);
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
            File[] files = directory.listFiles(pathname ->
                    pathname.getName().endsWith(Format.OLE2.getFileExtension()) ||
                            pathname.getName().endsWith(Format.OOXML.getFileExtension()));

            if (files != null) {
                for (File file : files) {
                    try {
                        //-- We search the first empty Row
                        if (resourceItemSearch.indexOf(BatchExcelRow.EMPTY, new FileSystemResource(file)) != ResourceItemSearch.EOF) {
                            result = file;
                        }
                    } catch (InvalidFormatException e) {
                        throw new IOException("Excel format not supported for this workbook!", e);
                    } catch (Exception e) {
                        throw new IOException("Cannot find content in your Excel file", e);
                    }
                }
            }
        }


        return result;
    }

    public void setResourceItemSearch(ResourceItemSearch<BatchExcelRow> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
