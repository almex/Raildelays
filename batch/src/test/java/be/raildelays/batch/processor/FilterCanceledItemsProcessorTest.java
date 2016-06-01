package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FilterCanceledItemsProcessorTest {

    private FilterCanceledItemsProcessor processor;

    @Before
    public void setUp() {
        processor = new FilterCanceledItemsProcessor();
    }

    @Test
    public void testCanceled() throws Exception {
        Assert.assertNull(processor.process(
                new BatchExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL)
                        .canceled(true)
                        .build(false))
        );
    }

    @Test
    public void testNotCanceled() throws Exception {
        Assert.assertNotNull(processor.process(
                new BatchExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL)
                        .canceled(false)
                        .build(false))
        );
    }

}