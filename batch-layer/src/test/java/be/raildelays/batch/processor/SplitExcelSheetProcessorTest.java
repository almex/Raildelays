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
    public void testFilteredBefore() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-12-31T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testNotFilteredBefore() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-01-01T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

    @Test
    public void testNotFilteredEquals() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-01-18T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

    @Test
    public void testFilteredEquals() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-01-18T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testFilteredAfter() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-01-01T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testNotFilteredAfter() throws Exception {
        processor.setMode(SplitExcelSheetProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(Date.from(Instant.parse("2014-12-31T00:00:00.00Z")), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

}
