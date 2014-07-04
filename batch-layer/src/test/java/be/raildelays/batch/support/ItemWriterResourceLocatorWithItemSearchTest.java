package be.raildelays.batch.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.poi.Format;
import be.raildelays.batch.poi.SimpleResourceItemSearch;
import be.raildelays.batch.reader.BatchExcelRowMapper;
import be.raildelays.batch.reader.ExcelSheetItemReader;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Almex
 */
public class ItemWriterResourceLocatorWithItemSearchTest extends AbstractFileTest {



    private ItemWriterResourceLocator resourceLocator;
    private ExcelSheetItemReader<BatchExcelRow> reader;

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

        resourceLocator.beforeChunk(new ChunkContext(new StepContext(stepExecution)));
        resourceLocator.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build()}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_NAME, resource.getFilename());
    }

    @Test
    public void testNotFoundExisting() throws IOException {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        reader.setMaxItemCount(1);

        resourceLocator.beforeChunk(new ChunkContext(new StepContext(stepExecution)));
        resourceLocator.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build()}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_PREFIX + " " + DATE_TO_STRING + Format.OLE2.getFileExtension(), resource.getFilename());
    }


    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
