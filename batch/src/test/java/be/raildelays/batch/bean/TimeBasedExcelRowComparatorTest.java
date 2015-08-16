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

    private Date now;

    @Override
    public void setUp() {
        comparator = new TimeBasedExcelRowComparator();
        now = new Date();
    }

    @Test
    public final void testCompareNullDate() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .build(false),
                        new BatchExcelRow.Builder(null, null)
                                .build(false)),
                lessThan(0));
    }

    @Test
    public void testCompareNullExpectedDepartureTime() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .expectedDepartureTime(new Date())
                                .build(false),
                        new BatchExcelRow.Builder(now, null)
                                .build(false)),
                lessThan(0));
    }

    @Test
    public void testCompareNullExpecteArrivalTime() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .expectedDepartureTime(now)
                                .expectedArrivalTime(now)
                                .build(false),
                        new BatchExcelRow.Builder(now, null)
                                .expectedDepartureTime(now)
                                .build(false)),
                lessThan(0));
    }
}