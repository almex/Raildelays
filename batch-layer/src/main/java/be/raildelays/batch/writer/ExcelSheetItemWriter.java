package be.raildelays.batch.writer;

import be.raildelays.batch.poi.RowAggregator;
import be.raildelays.batch.poi.WorkbookAction;
import be.raildelays.batch.support.AbstractItemCountingItemStreamItemWriter;
import be.raildelays.domain.support.ItemIndexAware;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;

public class ExcelSheetItemWriter<T> extends AbstractItemCountingItemStreamItemWriter<T> implements ResourceAwareItemWriterItemStream<T>, InitializingBean {

    protected OutputStream outputStream;

    protected Workbook workbook;

    protected int rowsToSkip = 0;

    protected RowAggregator rowAggregator;

    protected int sheetIndex;

    protected int rowIndex;

    protected Resource resource;

    protected Resource template;

    private boolean shouldDeleteIfExists = true;

    enum Format {
        OLE2(".xls"), OOXML(".xlsx");

        private String fileExtension;

        Format(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemWriter.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(resource,
                "You must provide an resource before using this bean");
    }

    @Override
    protected void jumpToItem(int itemIndex) throws Exception {
        super.jumpToItem(rowsToSkip + itemIndex);
    }

    @Override
    public void doOpen() throws Exception {
        try {
            File outputFile = resource.getFile();

            boolean deleted = true;

            if (template != null && !outputFile.exists()) {
                this.workbook = WorkbookFactory.create(template.getInputStream());
            } else {
                this.workbook = WorkbookFactory.create(resource.getInputStream());
            }


            if (outputFile.exists() && shouldDeleteIfExists) {
                deleted = outputFile.delete();

                LOGGER.debug("Output file '{}' deleted:{}", outputFile.getAbsolutePath(), deleted);
            } else if (deleted) {
                boolean created = outputFile.createNewFile();

                LOGGER.debug("Output file '{}' created:{}", outputFile.getAbsolutePath(), created);
            }

            this.outputStream = new FileOutputStream(outputFile);

        } catch (IOException | InvalidFormatException e) {
            throw new ItemStreamException("I/O when opening Excel template", e);
        }
    }

    @Override
    public boolean doWrite(T item, int itemIndex) throws Exception {
        ExcelRow previousRow = null;

        try {
            rowIndex = rowsToSkip + itemIndex;
            rowAggregator.aggregate(item, workbook, sheetIndex, rowIndex);
        } catch (Exception e) {
            LOGGER.error("We were not able to write in the Excel file", e);
        }

        return previousRow == null;
    }

    @Override
    public void doClose() throws ItemStreamException {

        try {
            if (outputStream != null && workbook != null) {
                workbook.write(outputStream);
                workbook = null;
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

            try {
                if (template != null) {
                    template.getInputStream().close();
                }
            } catch (IOException e) {
                LOGGER.error("I/O error when closing Excel templatePath file", e);
            }
        }


    }

    protected void setCurrentRowIndex(int rowIndex) {
        setCurrentItemCount(rowIndex - rowsToSkip);
    }

    protected Sheet getCurrentSheet() {
        return workbook != null ? workbook.getSheetAt(sheetIndex) : null;
    }

    public void setRowsToSkip(int rowsToSkip) {
        this.rowsToSkip = rowsToSkip;
    }

    public void setRowAggregator(RowAggregator rowAggregator) {
        this.rowAggregator = rowAggregator;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setTemplate(Resource template) {
        this.template = template;
    }

    public void setShouldDeleteIfExists(boolean shouldDeleteIfExists) {
        this.shouldDeleteIfExists = shouldDeleteIfExists;
    }
}
