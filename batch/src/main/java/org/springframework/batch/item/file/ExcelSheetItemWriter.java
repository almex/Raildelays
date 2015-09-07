package org.springframework.batch.item.file;

import org.apache.commons.lang.Validate;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.WriteFailedException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemWriter;
import org.springframework.batch.support.ResourceAwareItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

/**
 * @param <T> parameter type of the method {@link #write(java.util.List)}
 */
public class ExcelSheetItemWriter<T> extends AbstractItemCountingItemStreamItemWriter<T> implements ResourceAwareItemWriterItemStream<T>, InitializingBean, ResourceAwareItemStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetItemWriter.class);
    private static final String ROW_TO_SKIP_KEY = "row.to.skip.key";
    private static final String SHEET_INDEX_KEY = "sheet.index.key";
    protected RowAggregator<T> rowAggregator;
    protected Resource resource;
    protected Resource template;
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
    protected void jumpToItem(int itemIndex) throws ItemStreamException {
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
    public void doOpen() throws ItemStreamException {
        try {
            Path outputFile = resource.getFile().toPath();


            if (Files.exists(outputFile) && (shouldDeleteIfExists || !isValidExcelFile(outputFile.toFile()))) {
                boolean deleted = Files.deleteIfExists(outputFile);

                LOGGER.debug("Output file '{}' deleted:{}", outputFile.toAbsolutePath(), deleted);
            }

            boolean created = false;
            if (Files.notExists(outputFile)) {
                Path directory = outputFile.toAbsolutePath().getParent();

                if (Files.notExists(directory)) {
                    Files.createDirectories(directory);
                }

                Files.createFile(outputFile);

                /**
                 * To avoid an odd behaviour on Windows where the system can cache the creation time,
                 * making difficult to test if if we've already deleted the file or not.
                 */
                Files.setAttribute(outputFile, "basic:creationTime", FileTime.from(Instant.now()), LinkOption.NOFOLLOW_LINKS);
                created = Files.exists(outputFile);
                jumpToItem(0);

                LOGGER.debug("Output file '{}' created:{}", outputFile.toAbsolutePath(), created);
            }

            if (template != null && created) {
                try (InputStream inputStream = template.getInputStream()) {
                    workbook = WorkbookFactory.create(inputStream);
                }
            } else {
                if (created) {
                    String fileName = outputFile.getFileName().toString();

                    if (fileName.endsWith(Format.OLE2.getFileExtension())) {
                        workbook = new HSSFWorkbook();
                    } else if (fileName.endsWith(Format.OOXML.getFileExtension())) {
                        workbook = new XSSFWorkbook();
                    } else {
                        throw new InvalidFormatException("Your output is neither an OLE2 format, nor an OOXML format");
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

            /**
             * We write our first bytes after reading the template or creating the new Workbook.
             */
            writeToFile();
        } catch (IOException e) {
            throw new ItemStreamException("I/O exception when opening the Excel file", e);
        } catch (InvalidFormatException e) {
            throw new ItemStreamException("Invalid format exception when opening the Excel file", e);
        }
    }

    @Override
    public boolean doWrite(T item) throws ItemStreamException {
        T previousRow = null;

        if (item != null) {
            try {
                previousRow = rowAggregator.aggregate(item, workbook, sheetIndex, getCurrentItemIndex());

                writeToFile();

                LOGGER.trace("Previous row={}", previousRow);
            } catch (Exception e) {
                throw new WriteFailedException("We were not able to write in the Excel file", e);
            }
        }

        return previousRow == null;
    }

    @Override
    public void doClose() throws ItemStreamException {

        try {
            if (workbook != null) {
                writeToFile();
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when writing Excel outputDirectory file", e);
        } finally {
            IOUtils.closeQuietly(workbook);
            workbook = null;
        }


    }

    private void writeToFile() throws IOException {
        try {
            writeToFile(true);
        } catch (InterruptedException e) {
            throw new IOException("The attempt to wait for a second try to write to the Excel file failed", e);
        }
    }

    private void writeToFile(boolean firstTime) throws IOException, InterruptedException {
        try (OutputStream output = Files.newOutputStream(resource.getFile().toPath())) {
            workbook.write(output);
        } catch (IOException e) {
            /**
             * FIXME
             * This part of the code is a workaround to some test failure where file get apparently locked by another
             * process. Don't how and when it does happen. It seems only happening on Windows OS.
             */
            Thread.sleep(1000);

            if (firstTime) {
                writeToFile(false);
            } else {
                throw e;
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

    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getTemplate() {
        return template;
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
