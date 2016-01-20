package be.raildelays.batch.bean;


import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

/**
 * @author Almex
 */
public class ExcelRowComparatorTest extends AbstractExcelRowComparatorTest {

    private ExcelRow lho;
    private ExcelRow rho;

    @Before
    public void setUp() throws Exception {
        comparator = new ExcelRowComparator();
        ExcelRow.Builder builder = new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .departureStation(new Station("departureStation"))
                .linkStation(new Station("linkStation"))
                .arrivalStation(new Station("arrivalStation"))
                .expectedDepartureTime(LocalTime.of(10, 1))
                .expectedArrivalTime(LocalTime.of(11, 0))
                .expectedTrain1(new Train("expectedTrain1"))
                .expectedTrain2(new Train("expectedTrain2"))
                .effectiveDepartureTime(LocalTime.of(10, 5))
                .effectiveArrivalTime(LocalTime.of(11, 0))
                .effectiveTrain1(new Train("expectedTrain1"))
                .effectiveTrain2(new Train("effectiveTrain2"))
                .delay(0L);
        lho = builder.build();
        rho = builder.build();
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values then the
     * {@link ExcelRowComparator} considers them as identical.
     */
    @Test
    public void testEquals() throws Exception {
        Assert.assertEquals(0, comparator.compare(lho, rho));
    }

    /**
     * We expect that an EMPTY BatchExcelRow match an empty row.
     */
    @Test
    public void testEqualsEmpty() throws Exception {
        Assert.assertEquals(0, comparator.compare(new BatchExcelRow.Builder(null, null).delay(0L).build(false), BatchExcelRow.EMPTY));
    }

    /**
     * We expect that if the two {@link ExcelRow} are the same reference the
     * {@link ExcelRowComparator} considers them as identical.
     */
    @Test
    public void testEqualsReferences() throws Exception {
        Assert.assertThat(comparator.compare(lho, lho), is(equalTo(0)));
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values except one then
     * the natural order of that value should define if it's balanced on the left or on the right.
     */
    @Test
    public void testLess() throws Exception {
        rho.setDelay(15L);
        Assert.assertThat(comparator.compare(lho, rho), is(lessThan(0)));
    }

    /**
     * We expect that if the two {@link ExcelRow} are not the same reference but contain same values except one then
     * the natural order of that value should define if it's balanced on the left or on the right.
     */
    @Test
    public void testGreater() throws Exception {
        lho.setDelay(15L);
        Assert.assertThat(comparator.compare(lho, rho), is(greaterThan(0)));
    }

    /**
     * We expect that if the left hand object is null and that the right hand object is not then
     * {@link ExcelRowComparator#compare(ExcelRow, ExcelRow)} return a negative value.
     */
    @Test
    public void testWithNullOnLeft() throws Exception {
        Assert.assertThat(comparator.compare(null, rho), is(greaterThan(0)));
    }

    /**
     * We expect that if the right hand object is null and that the left hand object is not then
     * {@link ExcelRowComparator#compare(ExcelRow, ExcelRow)} return a positive value.
     */
    @Test
    public void testWithNullOnRight() throws Exception {
        Assert.assertThat(comparator.compare(lho, null), is(lessThan(0)));
    }
}