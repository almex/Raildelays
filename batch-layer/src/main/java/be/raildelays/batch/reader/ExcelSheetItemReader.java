package be.raildelays.batch.reader;

import be.raildelays.batch.exception.ExcelRowMappingException;
import be.raildelays.batch.poi.RowMapper;
import be.raildelays.batch.support.IndexedResourceAwareItemStreamReader;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.*;

/**
 * @author Almex
 */
public class ExcelSheetItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements IndexedResourceAwareItemStreamReader<T>, InitializingBean {

    private RowMapper<T> rowMapper;

    private Resource resource;

    private Workbook workbook;

    private boolean noInput = false;

    private int rowsToSkip = 0;

    private int sheetIndex = 0;

    private static Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemReader.class);

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
                throw new ExcelRowMappingException("Parsing error at line: " + getCurrentIndex() + " in resource=["
                        + resource.getDescription() + "], input=[" + row + "]", ex, row, getCurrentIndex());
            }
        }

        return result;
    }

    /**
     * @return next line (skip comments).getCurrentResource
     */
    private Row readRow() {
        Row result = null;

        if (workbook == null) {
            throw new ReaderNotOpenException("Reader must be open before it can be read.");
        }

        result =  workbook.getSheetAt(sheetIndex).getRow(getCurrentIndex());
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

        if (!isValidExcelFile(resource.getFile()))    {
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
        InputStream inputStream = new FileInputStream(resource.getFile());
        try {
            this.workbook = WorkbookFactory.create(inputStream);
        } finally {
            inputStream.close(); //-- Everything is in the buffer we can close the file
        }

        noInput = false;
        jumpToItem(0);
    }

    private static boolean isValidExcelFile(File file) throws IOException {
        InputStream inputStream = new PushbackInputStream(new FileInputStream(file), 8);

        try {
            return POIFSFileSystem.hasPOIFSHeader(inputStream) || POIXMLDocument.hasOOXMLHeader(inputStream);
        } finally {
            inputStream.close();
        }
    }

    @Override
    protected void doClose() throws Exception {
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException {
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

    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }
}
