package org.springframework.batch.item.file;

import org.apache.commons.lang.Validate;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * @param <T> parameter type of the method {@link #write(java.util.List)}
 */
public class ExcelSheetItemWriter<T> extends AbstractItemCountingItemStreamItemWriter<T> implements ResourceAwareItemWriterItemStream<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemWriter.class);
    private static final String ROW_TO_SKIP_KEY = "row.to.skip.key";
    private static final String SHEET_INDEX_KEY = "sheet.index.key";
    protected RowAggregator<T> rowAggregator;
    protected Resource resource;
    protected Resource template;
    protected OutputStream outputStream;
    protected Workbook workbook;
    protected int rowsToSkip = 0;
    protected int sheetIndex = 0;
    private boolean shouldDeleteIfExists = false;

    private static boolean isValidExcelFile(File file) throws IOException {

        try (InputStream inputStream = new PushbackInputStream(new FileInputStream(file), 8)) {
            return POIFSFileSystem.hasPOIFSHeader(inputStream) || POIXMLDocument.hasOOXMLHeader(inputStream);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(rowAggregator,
                "You must provide a rowAggregator before using this bean");
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        super.jumpToItem(rowsToSkip + itemIndex);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);

        if (executionContext.containsKey(getExecutionContextKey(ROW_TO_SKIP_KEY))) {
            rowsToSkip = executionContext.getInt(getExecutionContextKey(ROW_TO_SKIP_KEY));
        }

        if (executionContext.containsKey(getExecutionContextKey(SHEET_INDEX_KEY))) {
            sheetIndex = executionContext.getInt(getExecutionContextKey(SHEET_INDEX_KEY));
        }
    }

    @Override
    public void doOpen() throws Exception {
        try {
            File outputFile = resource.getFile();

            if (outputFile.exists() && (shouldDeleteIfExists || !isValidExcelFile(outputFile))) {
                boolean deleted = outputFile.delete();

                LOGGER.debug("Output file '{}' deleted:{}", outputFile.getAbsolutePath(), deleted);
            }

            boolean created = false;
            if (!outputFile.exists()) {
                created = outputFile.createNewFile();
                jumpToItem(0);

                LOGGER.debug("Output file '{}' created:{}", outputFile.getAbsolutePath(), created);
            }

            if (template != null && created) {
                try (InputStream inputStream = template.getInputStream()) {
                    this.workbook = WorkbookFactory.create(inputStream);
                }
            } else {
                if (created) {
                    if (outputFile.getName().endsWith(Format.OLE2.getFileExtension())) {
                        this.workbook = new HSSFWorkbook();
                    } else if (outputFile.getName().endsWith(Format.OOXML.getFileExtension())) {
                        this.workbook = new XSSFWorkbook();
                    } else {
                        throw new InvalidFormatException("Your template is neither an OLE2 format, nor an OOXML format");
                    }
                } else {
                    /**
                     * ATTENTION: if we use the resource.getFileInputStream() the stream is never released!
                     * So, we create our own FileInputStream instead. Don't know why. Seems like a bug in Apache POI
                     */
                    try (InputStream inputStream = new FileInputStream(resource.getFile())) {
                        this.workbook = WorkbookFactory.create(inputStream);
                    }
                }
            }

            this.outputStream = new FileOutputStream(outputFile);

        } catch (IOException | InvalidFormatException e) {
            throw new ItemStreamException("I/O when opening Excel template", e);
        }
    }

    @Override
    public boolean doWrite(T item) throws Exception {
        T previousRow = null;

        if (item != null) {
            try {
                previousRow = rowAggregator.aggregate(item, workbook, sheetIndex, getCurrentItemIndex());
            } catch (Exception e) {
                LOGGER.error("We were not able to write in the Excel file", e);
            }
        }

        return previousRow == null;
    }

    @Override
    public void doClose() throws ItemStreamException {

        try {
            if (outputStream != null && workbook != null) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when writing Excel outputDirectory file", e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error when closing Excel outputDirectory file", e);
            }
        }


    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        if (isSaveState()) {
            if (rowsToSkip < Integer.MAX_VALUE) {
                executionContext.putInt(getExecutionContextKey(ROW_TO_SKIP_KEY), rowsToSkip);
            }

            executionContext.putInt(getExecutionContextKey(SHEET_INDEX_KEY), sheetIndex);
        }
    }

    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    public void setRowAggregator(RowAggregator<T> rowAggregator) {
        this.rowAggregator = rowAggregator;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setTemplate(Resource template) {
        this.template = template;
    }

    public void setShouldDeleteIfExists(boolean shouldDeleteIfExists) {
        this.shouldDeleteIfExists = shouldDeleteIfExists;
    }

    public enum Format {
        OLE2(".xls"), OOXML(".xlsx");

        private String fileExtension;

        Format(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }

}
