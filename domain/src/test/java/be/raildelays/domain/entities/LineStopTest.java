package be.raildelays.domain.entities;

import be.raildelays.delays.TimeDelay;
import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.*;

public class LineStopTest extends AbstractObjectTest {

    @DataPoint
    public static LineStop DATA_POINT1;

    @DataPoint
    public static LineStop DATA_POINT2;

    @DataPoint
    public static LineStop DATA_POINT3;

    @DataPoint
    public static LineStop DATA_POINT4;

    @Override
    @Before
    public void setUp() throws ParseException {
        TrainLine trainLine = new TrainLine.Builder(466L).build();
        Station station = new Station("Liège (Liège-Guillemins)");
        TimeDelay arrivalTime = TimeDelay.of(LocalTime.parse("12:00"), 5L);
        TimeDelay departureTime = TimeDelay.of(LocalTime.parse("12:05"), 5L);
        LocalDate date = LocalDate.now();

        DATA_POINT1 = new LineStop.Builder()
                .date(date)
                .trainLine(trainLine)
                .station(station)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .addNext(new LineStop.Builder().build(false))
                .addPrevious(new LineStop.Builder().build(false))
                .canceledArrival(false)
                .canceledDeparture(false)
                .build();

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new LineStop.Builder(DATA_POINT1).build();

        DATA_POINT4 = new LineStop.Builder().date(LocalDate.now())
                .trainLine(new TrainLine.Builder(469L).build())
                .station(new Station("Brussels (Bruxelles-central)"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledArrival(false)
                .canceledDeparture(false)
                .build();
    }

    @Test
    public void testCompareToEquals() {
        assertThat(DATA_POINT1.compareTo(DATA_POINT2), is(equalTo(0)));
    }

    @Test
    public void testCompareToGreater() {
        assertThat(DATA_POINT1.compareTo(DATA_POINT4), is(greaterThan(0)));
    }

    @Test
    public void testCompareToLess() {
        assertThat(DATA_POINT4.compareTo(DATA_POINT1), is(lessThan(0)));
    }

    @Test
    public void testAccessors() {
        assertNotNull(DATA_POINT1.getDepartureTime());
        assertNotNull(DATA_POINT1.getArrivalTime());
        assertNotNull(DATA_POINT1.getDate());
        assertNotNull(DATA_POINT1.getTrainLine());
        assertNotNull(DATA_POINT1.getLocation());
        assertNotNull(DATA_POINT1.getNext());
        assertNotNull(DATA_POINT1.getPrevious());
        assertFalse(DATA_POINT1.isCanceledDeparture());
        assertFalse(DATA_POINT1.isCanceledArrival());
        assertNull(DATA_POINT1.getId());
    }

}
