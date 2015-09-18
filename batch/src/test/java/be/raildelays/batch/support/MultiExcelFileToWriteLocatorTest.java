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
import org.springframework.batch.item.file.ResourceContext;
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
        } else {
            cleanUp();
        }

        copyFile();

        resourceLocator = new MultiExcelFileToWriteLocator();
        resourceLocator.setDirectory(new FileSystemResource(CURRENT_PATH));
        resourceLocator.setFilePrefix(EXCEL_FILE_PREFIX);
        resourceLocator.setFileExtension(Format.OLE2.getFileExtension());
        resourceLocator.setMaxItemCount(2);
    }

    @Test
    public void testFoundExisting() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setResourceItemSearch((item, resource) -> 0);

        resourceLocator.onOpen(context);
        resourceLocator.onWrite(new ExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL).build(false), context);


        Assert.assertNotNull(context.getResource());
        Assert.assertEquals(EXCEL_FILE_NAME, context.getResource().getFilename());
    }

    @Test
    public void testNotFoundExisting() throws Exception {
        ResourceContext context = new ResourceContext(new ExecutionContext(), "foo");

        resourceLocator.setResourceItemSearch((item, resource) -> -1);

        resourceLocator.onOpen(context);
        resourceLocator.onWrite(new ExcelRow.Builder(LocalDate.parse("2014-01-21"), Sens.ARRIVAL).build(false), context);

        Assert.assertNotNull(context.getResource());
        Assert.assertEquals(EXCEL_FILE_PREFIX + " " + DATE_TO_STRING + Format.OLE2.getFileExtension(), context.getResource().getFilename());
    }


    @After
    public void tearDown() throws InterruptedException {
        cleanUp();
    }

}
