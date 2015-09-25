package be.raildelays.batch.bean;

import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.OrderingComparison.greaterThan;

@RunWith(BlockJUnit4ClassRunner.class)
public class TimeBasedExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    private LocalTime now;
    private LocalDate today;

    @Override
    public void setUp() {
        comparator = new TimeBasedExcelRowComparator();
        now = LocalTime.now();
        today = LocalDate.now();
    }

    @Test
    public final void testCompareNullDate() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(today, null).build(false),
                        new BatchExcelRow.Builder(null, null).build(false)
                ),
                lessThan(0)
        );
    }

    @Test
    public final void testCompareNullOnLeft() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        null,
                        new BatchExcelRow.Builder(null, null).build(false)
                ),
                lessThan(0)
        );
    }

    @Test
    public final void testCompareNullOnRight() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(today, null).build(false),
                        null
                ),
                greaterThan(0)
        );
    }

    @Test
    public void testCompareNullExpectedDepartureTime() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(today, null).expectedDepartureTime(LocalTime.now()).build(false),
                        new BatchExcelRow.Builder(today, null).build(false)
                ),
                lessThan(0)
        );
    }

    @Test
    public void testCompareNullExpectedArrivalTime() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(today, null)
                                .expectedDepartureTime(now)
                                .expectedArrivalTime(now)
                                .build(false),
                        new BatchExcelRow.Builder(today, null)
                                .expectedDepartureTime(now)
                                .build(false)
                ),
                lessThan(0)
        );
    }
}