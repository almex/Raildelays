package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.Format;
import be.raildelays.batch.support.ExcelFileSystemResourceDecorator;
import be.raildelays.batch.support.ExcelFileResource;
import be.raildelays.batch.support.ResourceAwareItemStream;
import be.raildelays.domain.xls.ExcelRow;
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
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.List;

/**
 * @author Almex
 */
public class ExcelSheetExcelRowWriter implements ResourceAwareItemWriterItemStream<BatchExcelRow>, ResourceAwareItemStream, InitializingBean {

    protected ExcelFileResource<BatchExcelRow> resourceDecorator;

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

            // By comparing on new WorkbookSearch(null) fileExtension we are retrieving the first workbook containing the first free row.
            File file = resourceDecorator.getFile(firstItem);
            if (file == null ) {
                file = resourceDecorator.getFile();
            }

            if (file != null) {
                delegate.setCurrentItemIndex(resourceDecorator.getContentRowIndex());
                delegate.setResource(resourceDecorator);
            } else {
                delegate.setResource(resourceDecorator.createRelative(getFileName(firstItem)));
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

    public void setResourceDecorator(ExcelFileResource<BatchExcelRow> resourceDecorator) {
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
