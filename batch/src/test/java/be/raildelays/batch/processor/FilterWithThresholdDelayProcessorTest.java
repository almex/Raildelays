package be.raildelays.batch.processor;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class FilterWithThresholdDelayProcessorTest {

    private FilterWithThresholdDelayProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new FilterWithThresholdDelayProcessor();
        processor.setThreshold(60L);
    }

    @Test
    public void testLessThanFiltered() throws Exception {
        processor.setMode(FilterWithThresholdDelayProcessor.Mode.FILTER_LESS_THAN);

        Assert.assertNull(processor.process(new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .delay(59L)
                .build(false)));
    }

    @Test
    public void testLessThanNotFiltered() throws Exception {
        processor.setMode(FilterWithThresholdDelayProcessor.Mode.FILTER_LESS_THAN);

        Assert.assertNotNull(processor.process(new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .delay(60L)
                .build(false)));
    }

    @Test
    public void testGreaterThanFiltered() throws Exception {
        processor.setMode(FilterWithThresholdDelayProcessor.Mode.FILTER_GREATER_OR_EQUAL_TO);

        Assert.assertNull(processor.process(new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .delay(60L)
                .build(false)));
    }

    @Test
    public void testGreaterThanNotFiltered() throws Exception {
        processor.setMode(FilterWithThresholdDelayProcessor.Mode.FILTER_GREATER_OR_EQUAL_TO);

        Assert.assertNotNull(processor.process(new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .delay(59L)
                .build(false)));
    }
}