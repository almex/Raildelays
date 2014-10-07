package be.raildelays.batch.bean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class TimeBasedBatchExcelRowComparatorTest {

    private TimeBasedBatchExcelRowComparator comparator = new TimeBasedBatchExcelRowComparator();

    @Test
    public void testCompareNull() throws Exception {
        Assert.assertEquals(-1, comparator.compare(new BatchExcelRow.Builder(null, null)
                        .build(false),
                null));
    }

    @Test
    public void testCompareNullDate() throws Exception {
        Assert.assertEquals(-1, comparator.compare(new BatchExcelRow.Builder(new Date(), null)
                        .build(false),
                new BatchExcelRow.Builder(null, null)
                        .build(false)));
    }

    @Test
    public void testCompareNullExpectedDepartureTime() throws Exception {
        Assert.assertEquals(-1, comparator.compare(new BatchExcelRow.Builder(new Date(), null)
                        .expectedDepartureTime(new Date())
                        .build(false),
                new BatchExcelRow.Builder(new Date(), null)
                        .build(false)));
    }

    @Test
    public void testCompareNullExpecteArrivalTime() throws Exception {
        Assert.assertEquals(-1, comparator.compare(new BatchExcelRow.Builder(new Date(), null)
                        .expectedDepartureTime(new Date())
                        .expectedArrivalTime(new Date())
                        .build(false),
                new BatchExcelRow.Builder(new Date(), null)
                        .expectedDepartureTime(new Date())
                        .build(false)));
    }
}