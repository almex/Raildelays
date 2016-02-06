package be.raildelays.batch.gtfs;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.net.URL;
import java.util.SimpleTimeZone;

/**
 * @author Almex
 */
public class StopTest extends AbstractObjectTest {

    @DataPoint
    public static Stop DATA_POINT1;
    @DataPoint
    public static Stop DATA_POINT2;
    @DataPoint
    public static Stop DATA_POINT3;
    @DataPoint
    public static Stop DATA_POINT4;

    @Before
    public void setUp() throws Exception {
        DATA_POINT1 = new Stop();
        DATA_POINT1.setStopName("Liège-Guillemins");
        DATA_POINT1.setStopId("1644");
        DATA_POINT1.setLocationType(Stop.LocationType.NOT_PHYSICAL);
        DATA_POINT1.setStopCode("foo");
        DATA_POINT1.setParentStation(null);
        DATA_POINT1.setStopDesc("bar");
        DATA_POINT1.setStopLat("0.000");
        DATA_POINT1.setStopLon("0.000");
        DATA_POINT1.setStopTimezone(SimpleTimeZone.getDefault());
        DATA_POINT1.setStopUrl(new URL("http://www.github.com"));
        DATA_POINT1.setWheelchairBoarding(Stop.Accessibility.ALLOWED);
        DATA_POINT1.setZoneId("25");

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new Stop();
        DATA_POINT3.setStopName(DATA_POINT1.getStopName());
        DATA_POINT3.setStopId(DATA_POINT1.getStopId());
        DATA_POINT3.setLocationType(DATA_POINT1.getLocationType());
        DATA_POINT3.setStopCode(DATA_POINT1.getStopCode());
        DATA_POINT3.setParentStation(DATA_POINT1.getParentStation());
        DATA_POINT3.setStopDesc(DATA_POINT1.getStopDesc());
        DATA_POINT3.setStopLat(DATA_POINT1.getStopLat());
        DATA_POINT3.setStopLon(DATA_POINT1.getStopLon());
        DATA_POINT3.setStopTimezone(DATA_POINT1.getStopTimezone());
        DATA_POINT3.setStopUrl(DATA_POINT1.getStopUrl());
        DATA_POINT3.setWheelchairBoarding(DATA_POINT1.getWheelchairBoarding());
        DATA_POINT3.setZoneId(DATA_POINT1.getZoneId());

        DATA_POINT4 = new Stop();
        DATA_POINT4.setStopName("Bruxelles-Central");
        DATA_POINT4.setStopId("1432");
        DATA_POINT4.setLocationType(Stop.LocationType.PHYSICAL);
        DATA_POINT4.setStopCode("foo");
        DATA_POINT4.setParentStation(DATA_POINT1.getStopId());
        DATA_POINT4.setStopDesc("bar");
        DATA_POINT4.setStopLat("0.001");
        DATA_POINT4.setStopLon("0.001");
        DATA_POINT4.setStopTimezone(SimpleTimeZone.getDefault());
        DATA_POINT4.setStopUrl(new URL("http://www.github.com"));
        DATA_POINT4.setWheelchairBoarding(Stop.Accessibility.NOT_ALLOWED);
        DATA_POINT4.setZoneId("25");
    }
}