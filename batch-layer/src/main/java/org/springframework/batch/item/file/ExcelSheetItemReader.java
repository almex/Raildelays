package org.springframework.batch.item.file;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.*;

/**
 * <p>
 * This {@link org.springframework.batch.item.ItemStreamReader} is capable of reading one sheet of one Excel file,
 * row per row, and mapping each row to a Java bean via a {@link RowMapper}.
 * This implementation make usage of Apache POI core framework to read either OLE2 or OOXML format (detection is
 * based on file content and not on file extension).
 * </p>
 * <p>
 * If you keep default settings, you only need to specify a
 * {@link org.springframework.core.io.Resource} and a {@link RowMapper}.</p>
 * <p>
 * Note that the restartability of this reader is only based on
 * {@link org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader}.
 * </p>
 *
 * @param <T> return type of a {@link #read()}
 * @author Almex
 * @see #setRowsToSkip(int)
 * @see #setSheetIndex(int)
 * @see
 * @since 1.1
 */
public class ExcelSheetItemReader<T> extends AbstractItemCountingItemStreamItemReader<T>
        implements IndexedResourceAwareItemStreamReader<T>, InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemReader.class);
    private RowMapper<T> rowMapper;
    private Resource resource;
    private Workbook workbook;
    private boolean noInput = false;
    private int rowsToSkip = 0;
    private int sheetIndex = 0;

    /**
     * Validate if the {@link java.io.File} is of a supported format (i.e.: OLE2 or OOXML).
     *
     * @param file targeting the Excel workbook to validate.
     * @return <code>true</code> if the format is supported, <code>false</code> otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private static boolean isValidExcelFile(File file) throws IOException {
        try (InputStream inputStream = new PushbackInputStream(new FileInputStream(file), 8)) {
            return POIFSFileSystem.hasPOIFSHeader(inputStream) || POIXMLDocument.hasOOXMLHeader(inputStream);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rowMapper, "rowMapper is required");
    }

    @Override
    protected T doRead() throws Exception {
        T result = null;

        Row row = readRow();

        if (row != null) {
            try {
                result = rowMapper.mapRow(row, getCurrentIndex());
            } catch (Exception ex) {
                throw new RowMappingException("Parsing error at line: " + getCurrentIndex() + " in resource=["
                        + resource.getDescription() + "], input=[" + row + "]", ex, row, getCurrentIndex());
            }
        }

        return result;
    }

    /**
     * @return next line (skipping the number of row specified by {@link #setRowsToSkip(int)}.
     */
    private Row readRow() {
        Row result;

        if (workbook == null) {
            throw new ReaderNotOpenException("Reader must be open before it can be read.");
        }

        result = workbook.getSheetAt(sheetIndex).getRow(getCurrentIndex());

        if (result == null) {
            noInput = true;
        }

        return result;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");

        noInput = true;
        if (!resource.exists()) {
            LOGGER.warn("Input resource does not exist {}", resource.getDescription());
            return;
        }

        if (!isValidExcelFile(resource.getFile())) {
            LOGGER.warn("Input resource is neither an OLE2 file, nor an OOXML file {}", resource.getDescription());
            return;
        }

        if (!resource.isReadable()) {
            LOGGER.warn("Input resource is not readable {}", resource.getDescription());
            return;
        }

        /**
         * ATTENTION: if we use the resource.getFileInputStream() the stream is never released!
         * So, we create our own FileInputStream instead. Don't know why. Seems like a bug in Apache POI
         */
        try (InputStream inputStream = new FileInputStream(resource.getFile())) {
            this.workbook = WorkbookFactory.create(inputStream);
        }

        noInput = false;
        jumpToItem(0);
    }

    @Override
    protected void doClose() throws Exception {
        noInput = true;
    }

    @Override
    public T read() throws Exception {
        T result = null;

        if (!noInput) {
            result = super.read();
            if (result == null) {
                noInput = true;
            }
        }

        return result;
    }

    @Override
    public int getCurrentIndex() {
        int result = -1;

        if (!noInput) {
            result = getCurrentItemCount() + rowsToSkip - 1;
        }

        return result;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Set the {@link RowMapper} used to map a {@link org.apache.poi.ss.usermodel.Row}
     * to your <code>T</code> type.
     *
     * @param rowMapper an implementation of {@link RowMapper}
     */
    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    /**
     * By default this value is initialized to 0.
     *
     * @param rowsToSkip set the number of the first row to read -1.
     */
    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    /**
     * By default this value is initialized to 0.
     *
     * @param sheetIndex set the zero-indexed based sheet.
     */
    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
