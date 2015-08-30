package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.Delays;
import be.raildelays.domain.Sens;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

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
                .Builder(LocalDate.now(), Sens.ARRIVAL)
                .delay(Delays.toMillis(14L))
                .build(false)));
    }

    @Test
    public void testNotFilter() throws Exception {
        Assert.assertNotNull(processor.process(new BatchExcelRow
                .Builder(LocalDate.now(), Sens.ARRIVAL)
                .delay(Delays.toMillis(15L))
                .build(false)));
    }
}