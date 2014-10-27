package be.raildelays.batch.bean;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static org.hamcrest.Matchers.greaterThan;

public class StationBasedExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    private Date now;

    @Override
    public void setUp() {
        comparator = new StationBasedExcelRowComparator(Language.EN);
        now = new Date();
    }


    @Test
    public final void testCompareNullDate() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .build(false),
                        new BatchExcelRow.Builder(null, null)
                                .build(false)),
                greaterThan(0));
    }

    @Test
    public void testCompareNullDepartureStation() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .departureStation(new Station("foo"))
                                .build(false),
                        new BatchExcelRow.Builder(now, null)
                                .build(false)),
                greaterThan(0));
    }

    @Test
    public void testCompareNullArrivalStation() throws Exception {
        Assert.assertThat(comparator.compare(new ExcelRow.Builder(now, null)
                                .departureStation(new Station("foo"))
                                .arrivalStation(new Station("bar"))
                                .build(false),
                        new BatchExcelRow.Builder(now, null)
                                .departureStation(new Station("foo"))
                                .build(false)),
                greaterThan(0));
    }
}