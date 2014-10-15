package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Date;

import static org.hamcrest.Matchers.lessThan;

@RunWith(BlockJUnit4ClassRunner.class)
public class TimeBasedExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    @Override
    public void setUp() {
        comparator = new TimeBasedExcelRowComparator();
    }

    @Test
    public final void testCompareNullDate() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(new Date(), null)
                                .build(false),
                        new BatchExcelRow.Builder(null, null)
                                .build(false)),
                lessThan(0));
    }

    @Test
    public void testCompareNullExpectedDepartureTime() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(new Date(), null)
                                .expectedDepartureTime(new Date())
                                .build(false),
                        new BatchExcelRow.Builder(new Date(), null)
                                .build(false)),
                lessThan(0));
    }

    @Test
    public void testCompareNullExpecteArrivalTime() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(new Date(), null)
                                .expectedDepartureTime(new Date())
                                .expectedArrivalTime(new Date())
                                .build(false),
                        new BatchExcelRow.Builder(new Date(), null)
                                .expectedDepartureTime(new Date())
                                .build(false)),
                lessThan(0));
    }
}