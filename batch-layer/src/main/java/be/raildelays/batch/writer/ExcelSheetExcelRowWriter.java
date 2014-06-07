package be.raildelays.batch.writer;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.Format;
import be.raildelays.batch.poi.WorkbookSearch;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.batch.support.ResourceAwareItemStream;
import be.raildelays.domain.xls.ExcelRow;
import groovy.lang.IllegalPropertyAccessException;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;

/**
 * @author Almex
 */
public class ExcelSheetExcelRowWriter extends ExcelSheetItemWriter<BatchExcelRow> implements ResourceAwareItemStream {

    protected String outputDirectory;

    protected boolean recoveryMode = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetExcelRowWriter.class);

    private ExecutionContext executionContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(outputDirectory,
                "You must provide an outputDirectory before using this bean");
        Validate.notNull(template,
                "You must provide a template before using this bean");
    }

    @Override
    public boolean doWrite(BatchExcelRow item) throws Exception {
        if (this.resource == null) {
            if (!isExistingWorkbooks(item)) {
                createNewWorkbook(getFileName(item));
            }
            super.doOpen();
        } else if (getCurrentItemCount() % getMaxItemCount() == 0) {
            doClose();
            createNewWorkbook(getFileName(item));
            super.doOpen();
        }

        return super.doWrite(item);
    }

    @Override
    public void doOpen() {
        //-- We manage creation of file in doWrite()
        resource = null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);

        this.executionContext = executionContext;
    }

    protected void createNewWorkbook(String fileName) throws Exception {
        resource = new FileSystemResource(new File(outputDirectory + File.separator + fileName));
    }

    private boolean isExistingWorkbooks(BatchExcelRow firstItem) throws Exception {
        Validate.notNull(firstItem, "You must provide the first ExcelRow of this Excel sheet prior to check " +
                "if a file already exists.");

        // By comparing on new WorkbookSearch(null) fileExtension we are retrieving the first workbook containing the first free row.
        recoveryMode = retrieveFirstRowContaining(firstItem) || retrieveFirstRowContaining(null);

        return recoveryMode;
    }

    private boolean retrieveFirstRowContaining(BatchExcelRow content) throws Exception {
        File directory = new File(outputDirectory);
        this.resource = null;

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + outputDirectory + "' parameter must be a directory path and nothing else.");

        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
            }
        })) {
            try {
                final Workbook currentWorkbook = WorkbookFactory.create(file);
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
                    this.resource = new FileSystemResource(file);
                    jumpToItem(currentRowIndex - rowsToSkip);
                    break;
                }
            } catch (InvalidFormatException e) {
                LOGGER.error("Excel format not supported for this workbook!", e);
            } catch (IOException e) {
                LOGGER.error("Error when opening an Excel workbook", e);
            }
        }

        return this.resource != null;
    }

    private String getFileName(ExcelRow firstItem) throws InvalidFormatException, IOException {
        String fileExtension = Format.OOXML.getFileExtension();
        InputStream inputStream = null;

        try {
            inputStream = new PushbackInputStream(template.getInputStream(), 8);

            if (POIFSFileSystem.hasPOIFSHeader(inputStream)) {
                fileExtension = Format.OLE2.getFileExtension();
            } else if (!POIXMLDocument.hasOOXMLHeader(inputStream)) {
                throw new InvalidFormatException("Your template is neither an OLE2 format, nor an OOXML format");
            }

            return "retard_sncb " + DateFormatUtils.format(firstItem.getDate(), "yyyyMMdd") + fileExtension;
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("We were not able to determine the template format", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void setResource(Resource resource) {
        try {
            throw new IllegalPropertyAccessException(this.getClass().getField("resource"), this.getClass());
        } catch (NoSuchFieldException e) {
            LOGGER.error("No such field error", e);
        }
    }

    @Override
    public Resource getResource() {
        return resource;
    }
}
