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
import org.springframework.batch.item.resource.ResourceAwareItemStreamWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;

/**
 * <p>
 * This {@link AbstractItemCountingItemStreamItemWriter} is capable of writing one sheet of one Excel file,
 * row per row, and aggregating each row to a Java bean via a {@link RowAggregator}.
 * This implementation make usage of Apache POI core framework to write either OLE2 or OOXML format (detection is
 * based on file extension).
 * </p>
 * <p>
 * If you keep default settings, you only need to specify a
 * {@link org.springframework.core.io.Resource} and a {@link RowAggregator}.</p>
 * <p>
 * Note that the restartability of this writer is only based on
 * {@link AbstractItemCountingItemStreamItemWriter}.
 * </p>
 *
 * @param <T> parameter type of the method {@link #write(java.util.List)}
 * @author Almex
 * @see #setRowsToSkip(int)
 * @see #setSheetIndex(int)
 * @implSpec This implementation is not thread-safe
 * @since 1.1
 */
public class ExcelSheetItemWriter<T> extends AbstractItemCountingItemStreamItemWriter<T> implements
        ResourceAwareItemStreamWriter<T>,
        ResourceAwareItemWriterItemStream<T>,
        InitializingBean {

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
            Path outputPath = resource.getFile().toPath();
            boolean created = false;

            if (Files.exists(outputPath) && (shouldDeleteIfExists || !isValidExcelFile(outputPath.toFile()))) {
                boolean deleted = Files.deleteIfExists(outputPath);

                LOGGER.debug("Output file '{}' deleted={}", outputPath.toAbsolutePath(), deleted);
            }

            if (Files.notExists(outputPath)) {
                created = createFile(outputPath);
                jumpToItem(0);

                LOGGER.debug("Output file '{}' created={}", outputPath.toAbsolutePath(), created);
            }

            if (template != null && created) {
                workbook = openWorkbook(template.getFile().toPath());
            } else {
                if (created) {
                    String fileName = outputPath.getFileName().toString();

                    workbook = Format.fromFileExtension(fileName).newWorkbook();
                } else {
                    workbook = openWorkbook(resource.getFile().toPath());
                }
            }

            /**
             * We write our first bytes after read the template or created the new Workbook.
             */
            flush();
        } catch (IOException e) {
            throw new ItemStreamException("I/O exception when opening the Excel file", e);
        } catch (InvalidFormatException e) {
            throw new ItemStreamException("Invalid format exception when opening the Excel file", e);
        }
    }

    @Override
    public boolean doWrite(T item) throws Exception {
        T previousRow = null;

        if (item != null) {
            previousRow = rowAggregator.aggregate(item, workbook, sheetIndex, getCurrentItemIndex());

            flush();

            LOGGER.trace("Previous row={}", previousRow);
        }

        return previousRow == null;
    }

    @Override
    public void doClose() throws ItemStreamException {
        try {
            if (workbook != null) {
                flush();
            }
        } catch (IOException e) {
            throw new ItemStreamException("I/O error when writing Excel outputDirectory file", e);
        } finally {
            IOUtils.closeQuietly(workbook);
            workbook = null;
        }
    }

    private Workbook openWorkbook(Path path) throws IOException, InvalidFormatException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return WorkbookFactory.create(inputStream);
        }
    }

    private boolean createFile(Path outputPath) throws IOException {
        Path directory = outputPath.toAbsolutePath().getParent();

        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
        }

        Files.createFile(outputPath);

        /**
         * To avoid an odd behaviour on Windows where the system can cache the creation time,
         * making difficult to test if if we've already deleted the file or not.
         */
        Files.setAttribute(outputPath, "basic:creationTime", FileTime.from(Instant.now()), LinkOption.NOFOLLOW_LINKS);

        return Files.exists(outputPath);
    }

    private void flush() throws IOException {
        try (OutputStream output = Files.newOutputStream(resource.getFile().toPath())) {
            workbook.write(output);
            output.flush();
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
        OLE2(".xls") {
            @Override
            public Workbook newWorkbook() {
                return new HSSFWorkbook();
            }
        },

        OOXML(".xlsx") {
            @Override
            public Workbook newWorkbook() {
                return new XSSFWorkbook();
            }
        };

        private String fileExtension;

        Format(String fileExtension) {
            this.fileExtension = fileExtension;
        }

        public static Format fromFileExtension(String path) throws InvalidFormatException {
            Format result;

            if (Format.OLE2.match(path)) {
                result = Format.OLE2;
            } else if (Format.OOXML.match(path)) {
                result = Format.OOXML;
            } else {
                throw new InvalidFormatException("Your output is neither an OLE2 format, nor an OOXML format");
            }

            return result;
        }

        public abstract Workbook newWorkbook();

        public String getFileExtension() {
            return fileExtension;
        }

        public boolean match(String path) {
            return path.endsWith(fileExtension);
        }

    }

}
