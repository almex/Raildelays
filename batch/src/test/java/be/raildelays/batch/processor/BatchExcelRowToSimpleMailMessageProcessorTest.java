package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(BlockJUnit4ClassRunner.class)
public class BatchExcelRowToSimpleMailMessageProcessorTest {

    private BatchExcelRowToSimpleMailMessageProcessor processor;

    @Before
    public void setUp() {
        processor = new BatchExcelRowToSimpleMailMessageProcessor();
    }

    @Test
    public void testProcess() throws Exception {

        Assert.assertNotNull(processor.process(new BatchExcelRow
                .Builder(LocalDate.now(), Sens.ARRIVAL)
                .build(false)));
    }
}
