package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.Format;
import be.raildelays.batch.poi.WorkbookSearch;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.batch.support.FileSystemResourceDecorator;
import be.raildelays.batch.support.WritableResourceDecorator;
import be.raildelays.batch.support.ResourceAwareItemStream;
import be.raildelays.domain.xls.ExcelRow;
import groovy.lang.IllegalPropertyAccessException;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

import java.io.*;
import java.util.List;

/**
 * @author Almex
 */
public class ExcelSheetExcelRowWriter implements ResourceAwareItemWriterItemStream<BatchExcelRow>, ResourceAwareItemStream, InitializingBean {

    protected WritableResourceDecorator resourceDecorator;

    private ExecutionContext executionContext;

    private Resource template;

    private ExcelSheetItemWriter<BatchExcelRow> delegate;

    private boolean shouldDeleteIfExists = false;

    protected int rowsToSkip = 0;

    protected int sheetIndex = 0;

    private int maxItemCount = Integer.MAX_VALUE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetExcelRowWriter.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(resourceDecorator,
                "You must provide an resourceDecorator before using this bean");
        Validate.notNull(template,
                "You must provide a template before using this bean");
        delegate = new ExcelSheetItemWriter<>();
        delegate.setTemplate(template);
        delegate.setRowsToSkip(rowsToSkip);
        delegate.setRowAggregator(new BatchExcelRowAggregator());
        delegate.setMaxItemCount(maxItemCount);
        delegate.setSheetIndex(sheetIndex);
        delegate.setShouldDeleteIfExists(shouldDeleteIfExists);
    }

    @Override
    public void write(List<? extends BatchExcelRow> items) throws Exception {
        if (!items.isEmpty()) {
            BatchExcelRow firstItem = items.get(0);

            if (resourceDecorator.getFile() == null) {
                File file = getExistingWorkbooks(firstItem);
                if (file != null) {
                    delegate.setResource(resourceDecorator.createRelative(file.getName()));
                } else {
                    delegate.setResource(resourceDecorator.createRelative(getFileName(firstItem)));
                }
            }

            delegate.open(executionContext);
            if (delegate.getCurrentItemCount() % delegate.getMaxItemCount() == 0) {
                delegate.close();
                delegate.update(executionContext);
                delegate.setResource(resourceDecorator.createRelative(getFileName(firstItem)));
                delegate.open(executionContext);
            }
            delegate.write(items);
            delegate.update(executionContext);
            delegate.close();
        }

    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {
        resourceDecorator = new FileSystemResourceDecorator(resourceDecorator.getOutputDirectory());
    }

    private File getExistingWorkbooks(BatchExcelRow firstItem) throws Exception {
        File result = null;

        Validate.notNull(firstItem, "You must provide the first ExcelRow of this Excel sheet prior to check " +
                "if a file already exists.");

        // By comparing on new WorkbookSearch(null) fileExtension we are retrieving the first workbook containing the first free row.
        result = retrieveFirstRowContaining(firstItem);
        if (result == null ) {
            result = retrieveFirstRowContaining(null);
        }

        return result;
    }

    private File retrieveFirstRowContaining(BatchExcelRow content) throws Exception {
        File result = null;
        File directory = resourceDecorator.getOutputDirectory().getFile();

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + resourceDecorator + "' parameter must be a directory path and nothing else.");

        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
            }
        })) {
            try {
                ExcelSheetItemReader<BatchExcelRow> reader = new ExcelSheetItemReader<>();
                WorkbookSearch<BatchExcelRow> container = new WorkbookSearch<>(executionContext);
                reader.setResource(new FileSystemResource(file));
                reader.setName(file.getName());
                reader.setRowMapper(new BatchExcelRowMapper());
                reader.setRowsToSkip(rowsToSkip);
                reader.setSaveState(false);
                container.setReader(reader);
                container.afterPropertiesSet();

                int currentRowIndex = container.indexOf(content);
                if (currentRowIndex != -1) {
                    result = file;
                    delegate.setCurrentItemIndex(currentRowIndex);
                    break;
                }
            } catch (InvalidFormatException e) {
                LOGGER.error("Excel format not supported for this workbook!", e);
            } catch (IOException e) {
                LOGGER.error("Error when opening an Excel workbook", e);
            }
        }

        return result;
    }

    private String getFileName(ExcelRow firstItem) throws InvalidFormatException, IOException {
        String fileExtension = Format.OOXML.getFileExtension();
        InputStream inputStream = null;
        PushbackInputStream pushbackInputStream = null;

        try {
            inputStream = new FileInputStream(template.getFile());
            pushbackInputStream = new PushbackInputStream(inputStream, 8);

            if (POIFSFileSystem.hasPOIFSHeader(pushbackInputStream)) {
                fileExtension = Format.OLE2.getFileExtension();
            } else if (!POIXMLDocument.hasOOXMLHeader(pushbackInputStream)) {
                throw new InvalidFormatException("Your template is neither an OLE2 format, nor an OOXML format");
            }

            return "retard_sncb " + DateFormatUtils.format(firstItem.getDate(), "yyyyMMdd") + fileExtension;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("We were not able to determine the template format", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (pushbackInputStream != null) {
                pushbackInputStream.close();
            }
        }
    }

    public void setResourceDecorator(WritableResourceDecorator resourceDecorator) {
        this.resourceDecorator = resourceDecorator;
    }

    @Override
    public Resource getResource() {
        return resourceDecorator;
    }

    @Override
    public void setResource(Resource resource)  {

    }

    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setShouldDeleteIfExists(boolean shouldDeleteIfExists) {
        this.shouldDeleteIfExists = shouldDeleteIfExists;
    }

    public void setMaxItemCount(int maxItemCount) {
        this.maxItemCount = maxItemCount;
    }

    public void setTemplate(Resource template) {
        this.template = template;
    }
}
