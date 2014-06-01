package be.raildelays.batch.writer;

import be.raildelays.batch.poi.WorkbookAction;
import be.raildelays.batch.poi.WorkbookSearch;
import be.raildelays.batch.reader.ExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import be.raildelays.domain.xls.ExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.core.io.FileSystemResource;

import java.io.*;
import java.util.List;

/**
 * @author Almex
 */
public class ExcelSheetExcelRowWriter extends ExcelSheetItemWriter<ExcelRow> {
    private static final int MAX_ITEM_PER_SHEET = 40;

    protected String outputDirectory;

    protected boolean recoveryMode = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelSheetExcelRowWriter.class);

    private ExecutionContext executionContext;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(outputDirectory,
                "You must provide an outputDirectory before using this bean");
        Validate.notNull(template,
                "You must provide a template before using this bean");
    }

    @Override
    public boolean doWrite(ExcelRow item, int itemIndex) throws Exception {
        if (workbook == null) {
            if (!isExistingWorkbooks(item)) {
                createNewWorkbook(getFileName(item));
            }
        }

        if (getCurrentItemCount() == MAX_ITEM_PER_SHEET) {
            doClose();
            createNewWorkbook(getFileName(item));
        }

        return super.doWrite(item, itemIndex);
    }

    @Override
    public void doOpen() {
        //-- We manage creation of file in doWrite()
    }

    protected void createNewWorkbook(String fileName) throws Exception {
        setResource(new FileSystemResource(new File(outputDirectory + File.separator + fileName)));
        super.doOpen();
    }

    private boolean isExistingWorkbooks(ExcelRow firstItem) throws Exception {
        Validate.notNull(firstItem, "You must provide the first ExcelRow of this Excel sheet prior to check " +
                "if a file already exists.");

        // By comparing on new WorkbookSearch(null) fileExtension we are retrieving the first workbook containing the first free row.
        recoveryMode = retrieveFirstRowContaining(firstItem) || retrieveFirstRowContaining(null);

        return recoveryMode;
    }

    private boolean retrieveFirstRowContaining(ExcelRow content) throws Exception {
        File directory = new File(outputDirectory);
        this.workbook = null;

        Validate.isTrue(directory.isDirectory(), "The outputDirectory '" + outputDirectory + "' parameter must be a directory path and nothing else.");

        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(Format.OLE2.getFileExtension()) || pathname.getName().endsWith(Format.OOXML.getFileExtension());
            }
        })) {
            try {
                final Workbook currentWorkbook = WorkbookFactory.create(file);
                ExcelSheetItemReader<ExcelRow> reader = new ExcelSheetItemReader<>();
                WorkbookSearch<ExcelRow> container = new WorkbookSearch<>(executionContext);
                reader.setResource(new FileSystemResource(file));
                reader.setName(file.getName());
                reader.setRowMapper(new ExcelRowMapper());
                reader.setRowsToSkip(rowsToSkip);
                reader.setSaveState(false);
                container.setReader(reader);
                container.afterPropertiesSet();

                int currentRowIndex = container.indexOf(content);
                if (currentRowIndex != -1) {
                    this.workbook = currentWorkbook;
                    this.outputStream = new FileOutputStream(file);

                    setCurrentRowIndex(currentRowIndex);
                    break;
                }
            } catch (InvalidFormatException e) {
                LOGGER.error("Excel format not supported for this workbook!", e);
            } catch (IOException e) {
                LOGGER.error("Error when opening an Excel workbook", e);
            }
        }

        return this.workbook != null;
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
    public void doClose() throws ItemStreamException {
        super.doClose();
    }


}
