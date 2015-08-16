package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class FilterSmallDelaysProcessorTest {

    private FilterSmallDelaysProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new FilterSmallDelaysProcessor();
        processor.setThreshold(15L);
        processor.afterPropertiesSet();
    }

    @Test
    public void testFilter() throws Exception {
        Assert.assertNull(processor.process(new BatchExcelRow
                .Builder(new Date(), Sens.ARRIVAL)
                .delay(14L)
                .build(false)));
    }

    @Test
    public void testNotFilter() throws Exception {
        Assert.assertNotNull(processor.process(new BatchExcelRow
                .Builder(new Date(), Sens.ARRIVAL)
                .delay(15L)
                .build(false)));
    }
}