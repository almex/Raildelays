package be.raildelays.batch.gtfs;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.util.Collections;

/**
 * @author Almex
 */
public class TripTest extends AbstractObjectTest {

    @DataPoint
    public static Trip DATA_POINT1;
    @DataPoint
    public static Trip DATA_POINT2;
    @DataPoint
    public static Trip DATA_POINT3;
    @DataPoint
    public static Trip DATA_POINT4;

    @Before
    public void setUp() throws Exception {
        DATA_POINT1 = new Trip();
        DATA_POINT1.setStopTimes(Collections.singletonList(new StopTime()));
        DATA_POINT1.setTripId("123");
        DATA_POINT1.setRouteId("123");
        DATA_POINT1.setBikesAllowed(Trip.Accessibility.ALLOWED);
        DATA_POINT1.setBlockId("123");
        DATA_POINT1.setDirectionId("123");
        DATA_POINT1.setServiceId("123");
        DATA_POINT1.setShapeId("123");
        DATA_POINT1.setTripHeadsign("foo");
        DATA_POINT1.setTripShortName("bar");
        DATA_POINT1.setWheelchairAccessible(Trip.Accessibility.NOT_ALLOWED);

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new Trip();
        DATA_POINT3.setStopTimes(DATA_POINT1.getStopTimes());
        DATA_POINT3.setTripId(DATA_POINT1.getTripId());
        DATA_POINT3.setRouteId(DATA_POINT1.getRouteId());
        DATA_POINT3.setBikesAllowed(DATA_POINT1.getBikesAllowed());
        DATA_POINT3.setBlockId(DATA_POINT1.getBlockId());
        DATA_POINT3.setDirectionId(DATA_POINT1.getDirectionId());
        DATA_POINT3.setServiceId(DATA_POINT1.getServiceId());
        DATA_POINT3.setShapeId(DATA_POINT1.getShapeId());
        DATA_POINT3.setTripHeadsign(DATA_POINT1.getTripHeadsign());
        DATA_POINT3.setTripShortName(DATA_POINT1.getTripShortName());
        DATA_POINT3.setWheelchairAccessible(DATA_POINT1.getWheelchairAccessible());

        DATA_POINT4 = new Trip();
        DATA_POINT4.setStopTimes(Collections.singletonList(new StopTime()));
        DATA_POINT4.setTripId("456");
        DATA_POINT4.setRouteId("456");
        DATA_POINT4.setBikesAllowed(Trip.Accessibility.valueForIndex(1));
        DATA_POINT4.setBlockId("456");
        DATA_POINT4.setDirectionId("456");
        DATA_POINT4.setServiceId("456");
        DATA_POINT4.setShapeId("456");
        DATA_POINT4.setTripHeadsign("foo");
        DATA_POINT4.setTripShortName("bar");
        DATA_POINT4.setWheelchairAccessible(Trip.Accessibility.valueForIndex(2));
    }
}