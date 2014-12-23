package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.Instant;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class SplitExcelSheetProcessorTest {

    private static Date THRESHOLD_DATE = Date.from(Instant.parse("2014-02-18T00:00:00.00Z"));
    private SplitExcelSheetProcessor processor;

    @Before
    public void setUp() {
        processor = new SplitExcelSheetProcessor();
        processor.setThresholdDate(THRESHOLD_DATE);
    }

    @Test
    public void testFiltered() throws Exception {
        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-01-01T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testNotFiltered() throws Exception {
        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-12-31T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

}
