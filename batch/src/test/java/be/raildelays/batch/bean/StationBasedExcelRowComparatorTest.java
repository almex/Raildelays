package be.raildelays.batch.bean;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class StationBasedExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    private LocalDate now;

    @Override
    public void setUp() {
        comparator = new StationBasedExcelRowComparator(Language.EN);
        now = LocalDate.now();
    }


    @Test
    public final void testCompareNullDate() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(now, null).build(false),
                        new BatchExcelRow.Builder(null, null).build(false)
                ),
                greaterThan(0)
        );
    }

    @Test
    public final void testCompareNullOnTheLeft() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        null,
                        new BatchExcelRow.Builder(null, null).build(false)
                ),
                lessThan(0)
        );
    }

    @Test
    public final void testCompareNullOnTheRight() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(now, null).build(false),
                        null
                ),
                greaterThan(0)
        );
    }

    @Test
    public void testCompareNullDepartureStation() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(now, null).departureStation(new Station("foo")).build(false),
                        new BatchExcelRow.Builder(now, null).build(false)
                ),
                greaterThan(0)
        );
    }

    @Test
    public void testCompareNullArrivalStation() throws Exception {
        Assert.assertThat(
                comparator.compare(
                        new ExcelRow.Builder(now, null)
                                .departureStation(new Station("foo"))
                                .arrivalStation(new Station("bar"))
                                .build(false),
                        new BatchExcelRow.Builder(now, null)
                                .departureStation(new Station("foo"))
                                .build(false)
                ),
                greaterThan(0)
        );
    }
}