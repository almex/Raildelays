package be.raildelays.batch.support;

import be.raildelays.batch.poi.Format;
import org.apache.commons.lang.Validate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.*;
import java.net.URI;
import java.net.URL;

/**
 * @author Almex
 */
public class ExcelFileSystemResourceDecorator<T extends Comparable<? super T>> extends FileSystemResourceDecorator implements ExcelFileResource<T> {

    private int currentRowIndex = 0;

    private ResourceItemSearch<T> resourceItemSearch;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileSystemResourceDecorator.class);

    public ExcelFileSystemResourceDecorator() {
        super();
    }

    public ExcelFileSystemResourceDecorator(Resource outputDirectory) {
        super(outputDirectory);
    }

    public ExcelFileSystemResourceDecorator(String outputDirectory) {
        super(outputDirectory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(resourceItemSearch,
                "You must provide a itemSearch before using this bean");
        super.afterPropertiesSet();
    }

    @Override
    public File getFile() throws IOException {
        return getFile(null);
    }

    @Override
    public File getFile(T content) throws IOException {
        File directory = getOutputDirectory().getFile();
        File result = null;

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + getOutputDirectory().getDescription() + "' parameter must be a directory path and nothing else.");

        if (getDelegate() == null) {
            try {
                for (File file : directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
                    }
                })) {
                    try {

                        int currentRowIndex = resourceItemSearch.indexOf(content, new FileSystemResource(file));

                        if (currentRowIndex != -1) {
                            createRelative(file.getName());
                            this.currentRowIndex = currentRowIndex;

                            result = getDelegate().getFile();
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
        } else {
            result = getDelegate().getFile();
        }

        if (result != null) {
            return result;
        } else {
            throw new FileNotFoundException("There is no exisiting Excel file found in this output directory: " + getOutputDirectory().getDescription());
        }
    }

    @Override
    public boolean exists() {
        try {
            getFile(); // We try to retrieve a file dynamically before saying it does not exist.

            return super.exists();
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public int getContentRowIndex() {
        return currentRowIndex;
    }

    public void setResourceItemSearch(ResourceItemSearch<T> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }
}
