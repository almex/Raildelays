package be.raildelays.batch.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.ExcelSheetItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.springframework.batch.item.file.ExcelSheetItemWriter.Format;

/**
 * @author Almex
 */
public class ItemWriterResourceLocatorWithItemSearchIT extends AbstractFileTest {


    private ItemWriterResourceLocator resourceLocator;
    private ExcelSheetItemReader<BatchExcelRow> reader;
    private ResourceLocatorListener listener;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);
        SimpleResourceItemSearch itemSearch = new SimpleResourceItemSearch();

        if (!directory.exists()) {
            directory.mkdir();
        } else {
            cleanUp();
        }

        copyFile();

        listener = new ResourceLocatorListener();

        reader = new ExcelSheetItemReader<>();
        reader.setName("test");
        reader.setSheetIndex(0);
        reader.setRowsToSkip(21);
        reader.setMaxItemCount(40);
        reader.setRowMapper(new BatchExcelRowMapper());

        itemSearch.setReader(reader);

        resourceLocator = new ItemWriterResourceLocator();
        resourceLocator.setResourceItemSearch(itemSearch);
        resourceLocator.setResource(new FileSystemResource(CURRENT_PATH + EXCEL_FILE_PREFIX + Format.OLE2.getFileExtension()));
    }


    @Test
    public void testFoundExisting() throws IOException {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        listener.beforeStep(stepExecution);
        listener.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build(false)}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_NAME, resource.getFilename());
    }

    @Test
    public void testNotFoundExisting() throws IOException {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        reader.setMaxItemCount(1);

        listener.beforeStep(stepExecution);
        listener.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build(false)}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_PREFIX + " " + DATE_TO_STRING + Format.OLE2.getFileExtension(), resource.getFilename());
    }


    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
