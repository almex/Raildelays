package be.raildelays.batch.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Almex
 */
public class ItemWriterResourceLocatorTest extends AbstractFileTest {



    private ItemWriterResourceLocator resourceLocator;

    private ResourceLocatorListener listener;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        } else {
            cleanUp();
        }

        copyFile();

        listener = new ResourceLocatorListener();
        resourceLocator = new ItemWriterResourceLocator();
        resourceLocator.setResource(new FileSystemResource(CURRENT_PATH + EXCEL_FILE_PREFIX + Format.OLE2.getFileExtension()));
    }

    @Test
    public void testFoundExisting() throws IOException {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        resourceLocator.setResourceItemSearch(new ResourceItemSearch() {
            @Override
            public int indexOf(Comparable item, Resource resource) throws Exception {
                return 0;
            }
        });

        listener.beforeStep(stepExecution);
        listener.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build()}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_NAME, resource.getFilename());
    }

    @Test
    public void testNotFoundExisting() throws IOException {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        resourceLocator.setResourceItemSearch(new ResourceItemSearch() {
            @Override
            public int indexOf(Comparable item, Resource resource) throws Exception {
                return -1;
            }
        });

        listener.beforeStep(stepExecution);
        listener.beforeWrite(Arrays.asList(new BatchExcelRow[]{new BatchExcelRow.Builder(DATE, null).build()}));
        Resource resource = resourceLocator.getResource(stepExecution.getExecutionContext());

        Assert.assertNotNull(resource);
        Assert.assertEquals(EXCEL_FILE_PREFIX + " " + DATE_TO_STRING + Format.OLE2.getFileExtension(), resource.getFilename());
    }


    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
