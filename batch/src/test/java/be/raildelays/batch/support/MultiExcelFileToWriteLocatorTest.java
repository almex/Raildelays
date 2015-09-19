package be.raildelays.batch.support;

import be.raildelays.batch.AbstractFileTest;
import be.raildelays.batch.writer.MultiExcelFileToWriteLocator;
import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.resource.ResourceContext;
import org.springframework.batch.item.resource.ResourceItemSearch;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.time.LocalDate;

import static org.springframework.batch.item.file.ExcelSheetItemWriter.Format;

/**
 * @author Almex
 */
public class MultiExcelFileToWriteLocatorTest extends AbstractFileTest {


    private MultiExcelFileToWriteLocator resourceLocator;

    @Before
    public void setUp() throws Exception {
        File directory = new File(CURRENT_PATH);

        if (!directory.exists()) {
            directory.mkdir();
        }

        cleanUp();
        copyFile();

        resourceLocator = new MultiExcelFileToWriteLocator();
        resourceLocator.setDirectory(new FileSystemResource(CURRENT_PATH));
        resourceLocator.setFilePrefix(EXCEL_FILE_PREFIX);
        resourceLocator.setFileExtension(Format.OLE2.getFileExtension());
        resourceLocator.setMaxItemCount(2);
    }

    /**
     * We expect to have a resource in the context if the ResourceItemSearch found something onOpen().
     */
    @Test
    public void testFoundExisting() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setResourceItemSearch((item, resource) -> 0);
        resourceLocator.onOpen(context);
        //resourceLocator.onWrite(new ExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL).build(false), context);

        Assert.assertNotNull(context.getResource());
        Assert.assertEquals(EXCEL_FILE_NAME, context.getResource().getFilename());
    }

    /**
     * We expect to have no resource in the context if the ResourceItemSearch found nothing onOpen().
     */
    @Test
    public void testNotFoundExisting() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setResourceItemSearch((item, resource) -> ResourceItemSearch.EOF);
        resourceLocator.onOpen(context);

        Assert.assertFalse(context.containsResource());
    }

    /**
     * We expect to have a resource in the context if the ResourceItemSearch found nothing onOpen() but create a new
     * file in onWrite().
     */
    @Test
    public void testCreateNewOne() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setResourceItemSearch((item, resource) -> ResourceItemSearch.EOF);
        resourceLocator.onOpen(context);

        Assert.assertFalse(context.containsResource());

        resourceLocator.onWrite(new ExcelRow.Builder(LocalDate.parse("2014-05-22"), Sens.ARRIVAL).build(false), context);

        Assert.assertEquals(EXCEL_FILE_NAME, context.getResource().getFilename());
    }

    /**
     * We expect that if we give a wrong path we get an IOException embedded into an ItemStreamException.
     */
    @Test(expected = ItemStreamException.class)
    public void testIOException() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setDirectory(new ClassPathResource("./foo"));
        resourceLocator.onOpen(context);
    }

    /**
     * We expect that if we give a null path we get an NullPointerException embedded into an ItemStreamException.
     */
    @Test(expected = ItemStreamException.class)
    public void testException() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setDirectory(null);
        resourceLocator.onOpen(context);
    }

    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
