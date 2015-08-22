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
import java.time.LocalDate;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class FilterWithThresholdDateProcessorTest {

    private static Date THRESHOLD_DATE = Date.from(Instant.parse("2014-02-18T00:00:00.00Z"));
    private FilterWithThresholdDateProcessor processor;

    @Before
    public void setUp() {
        processor = new FilterWithThresholdDateProcessor();
        processor.setThresholdDate(THRESHOLD_DATE);
    }

    @Test
    public void testFilteredBefore() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-12-31"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testNotFilteredBefore() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-01-01"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

    @Test
    public void testNotFilteredEquals() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.BEFORE);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-01-18"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

    @Test
    public void testFilteredEquals() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-01-18"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testFilteredAfter() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-01-01"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNull(result);
    }

    @Test
    public void testNotFilteredAfter() throws Exception {
        processor.setMode(FilterWithThresholdDateProcessor.Mode.AFTER_OR_EQUALS);

        BatchExcelRow item = new BatchExcelRow
                .Builder(LocalDate.parse("2014-12-31"), Sens.DEPARTURE)
                .build(false);
        ExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
    }

}
