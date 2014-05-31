package be.raildelays.batch.reader;

import be.raildelays.batch.poi.ExcelRowMappingException;
import be.raildelays.batch.poi.RowMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.*;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.InputStream;

/**
 * @author Almex
 */
public class ExcelSheetItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {

    private Resource resource;

    private RowMapper<T> rowMapper;

    private int rowsToSkip = 0;

    private int rowIndex = 0;

    private int sheetIndex = 0;

    private boolean noInput = false;

    private Workbook workbook;

    private Sheet sheet;

    private InputStream inputStream;

    private WorkbookFactory workbookFactory = new WorkbookFactory();

    private static Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemReader.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(rowMapper, "LineMapper is required");
    }

    @Override
    protected T doRead() throws Exception {
        T result = null;

        if (!noInput) {
            Row row = readRow();

            if (row != null) {
                try {
                    result = rowMapper.mapRow(row, rowIndex);
                } catch (Exception ex) {
                    throw new ExcelRowMappingException("Parsing error at line: " + rowIndex + " in resource=["
                            + resource.getDescription() + "], input=[" + row + "]", ex, row, rowIndex);
                }
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

        if (sheet == null) {
            sheet = getCurrentSheet();
        }

        result =  this.sheet.getRow(rowIndex);
        if (result != null) {
            rowIndex++;
        } else {
            noInput = true;
        }

        return result;
    }

    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");

        noInput = true;
        if (!resource.exists()) {
            LOGGER.warn("Input resource does not exist {}", resource.getDescription());
            return;
        }

        if (!resource.isReadable()) {
            LOGGER.warn("Input resource is not readable {}", resource.getDescription());
            return;
        }

        inputStream = resource.getInputStream();
        workbook = workbookFactory.create(inputStream);

        for (int i = 0; i < rowsToSkip; i++) {
            Row row = readRow();
        }
        noInput = false;
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        rowIndex = rowsToSkip + itemIndex;
    }

    @Override
    protected void doClose() throws Exception {
        rowIndex = 0;
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public Sheet getCurrentSheet() {
        return workbook != null ? workbook.getSheetAt(sheetIndex) : null;
    }

    public int getRowIndex() {
        return rowIndex;
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
