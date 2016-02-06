package be.raildelays.batch.gtfs;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.time.LocalTime;

/**
 * @author Almex
 */
public class StopTimeTest extends AbstractObjectTest {


    @DataPoint
    public static StopTime DATA_POINT1;
    @DataPoint
    public static StopTime DATA_POINT2;
    @DataPoint
    public static StopTime DATA_POINT3;
    @DataPoint
    public static StopTime DATA_POINT4;

    @Before
    public void setUp() throws Exception {
        DATA_POINT1 = new StopTime();
        DATA_POINT1.setStopId("4123");
        DATA_POINT1.setStopSequence(1);
        DATA_POINT1.setDepartureTime(LocalTime.now());
        DATA_POINT1.setArrivalTime(LocalTime.now());
        DATA_POINT1.setDropOffType(StopTime.Type.REGULARLY);
        DATA_POINT1.setPickupType(StopTime.Type.MUST_COORDINATE);
        DATA_POINT1.setShapeDistTraveled(0.0);
        DATA_POINT1.setStopHeadsign("foo");
        DATA_POINT1.setTimepoint(StopTime.Timepoint.APPROXIMATE);
        DATA_POINT1.setTripId("333");

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new StopTime();
        DATA_POINT3.setStopId(DATA_POINT1.getStopId());
        DATA_POINT3.setStopSequence(DATA_POINT1.getStopSequence());
        DATA_POINT3.setDepartureTime(DATA_POINT1.getDepartureTime());
        DATA_POINT3.setArrivalTime(DATA_POINT1.getArrivalTime());
        DATA_POINT3.setDropOffType(DATA_POINT1.getDropOffType());
        DATA_POINT3.setPickupType(DATA_POINT1.getPickupType());
        DATA_POINT3.setShapeDistTraveled(DATA_POINT1.getShapeDistTraveled());
        DATA_POINT3.setStopHeadsign(DATA_POINT1.getStopHeadsign());
        DATA_POINT3.setTimepoint(DATA_POINT1.getTimepoint());
        DATA_POINT3.setTripId(DATA_POINT1.getTripId());

        DATA_POINT4 = new StopTime();
        DATA_POINT4.setStopId("4123");
        DATA_POINT4.setStopSequence(1);
        DATA_POINT4.setDepartureTime(LocalTime.now());
        DATA_POINT4.setArrivalTime(LocalTime.now());
        DATA_POINT4.setDropOffType(StopTime.Type.MUST_PHONE);
        DATA_POINT4.setPickupType(StopTime.Type.NONE);
        DATA_POINT4.setShapeDistTraveled(0.0);
        DATA_POINT4.setStopHeadsign("foo");
        DATA_POINT4.setTimepoint(StopTime.Timepoint.EXACT);
        DATA_POINT4.setTripId("333");
    }
}