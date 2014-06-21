package be.raildelays.batch.support;

import be.raildelays.batch.poi.Format;
import org.apache.commons.lang.Validate;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * @author Almex
 */
public class ExcelFileSystemResourceDecorator<T extends Comparable<? super T>> extends FileSystemResourceDecorator implements ExcelFileResource<T> {

    private int currentRowIndex = 0;

    private ItemSearch<T> itemSearch;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelFileSystemResourceDecorator.class);

    public ExcelFileSystemResourceDecorator(Resource outputDirectory) {
        super(outputDirectory);
    }

    public ExcelFileSystemResourceDecorator(String outputDirectory) {
        super(outputDirectory);
    }

    private File file;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(itemSearch,
                "You must provide a itemSearch before using this bean");
    }

    @Override
    public File getFile() throws IOException {
        return getFile(null);
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        Resource result = super.createRelative(relativePath);

        this.file = super.getFile();

        return result;
    }

    @Override
    public File getFile(T content) throws IOException {
        File directory = getOutputDirectory().getFile();

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + getOutputDirectory().getDescription() + "' parameter must be a directory path and nothing else.");

        if (this.file == null) {
            try {
                for (File file : directory.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
                    }
                })) {
                    try {

                        int currentRowIndex = itemSearch.indexOf(content, new FileSystemResource(file));

                        if (currentRowIndex != -1) {
                            this.file = file;
                            this.currentRowIndex = currentRowIndex;
                            break;
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

        return this.file;
    }

    @Override
    public int getContentRowIndex() {
        return currentRowIndex;
    }

    public void setItemSearch(ItemSearch<T> itemSearch) {
        this.itemSearch = itemSearch;
    }
}
