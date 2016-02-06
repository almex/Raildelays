package be.raildelays.batch.gtfs;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.experimental.theories.DataPoint;

import java.net.URL;

/**
 * @author Almex
 */
public class RouteTest extends AbstractObjectTest {

    @DataPoint
    public static Route DATA_POINT1;

    @DataPoint
    public static Route DATA_POINT2;

    @DataPoint
    public static Route DATA_POINT3;

    @DataPoint
    public static Route DATA_POINT4;


    @Override
    public void setUp() throws Exception {
        DATA_POINT1 = new Route();
        DATA_POINT1.setRouteLongName("466 : Liège-Guillemins - Bruxelles-Central");
        DATA_POINT1.setRouteShortName("Liège-Guillemins - Bruxelles-Central");
        DATA_POINT1.setAgencyId("118");
        DATA_POINT1.setRouteColor("red");
        DATA_POINT1.setRouteDesc("bar");
        DATA_POINT1.setRouteId("466");
        DATA_POINT1.setRouteTextColor("red");
        DATA_POINT1.setRouteType(Route.RouteType.BUS);
        DATA_POINT1.setRouteUrl(new URL("http://www.github.com"));

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new Route();
        DATA_POINT2.setRouteLongName(DATA_POINT1.getRouteLongName());
        DATA_POINT2.setRouteShortName(DATA_POINT1.getRouteShortName());
        DATA_POINT2.setAgencyId(DATA_POINT1.getAgencyId());
        DATA_POINT2.setRouteColor(DATA_POINT1.getRouteColor());
        DATA_POINT2.setRouteDesc(DATA_POINT1.getRouteDesc());
        DATA_POINT2.setRouteId(DATA_POINT1.getRouteId());
        DATA_POINT2.setRouteTextColor(DATA_POINT1.getRouteTextColor());
        DATA_POINT2.setRouteType(DATA_POINT1.getRouteType());
        DATA_POINT2.setRouteUrl(DATA_POINT1.getRouteUrl());

        DATA_POINT4 = new Route();
        DATA_POINT4.setRouteLongName("515 : Liège-Guillemins - Bruxelles-Central");
        DATA_POINT4.setRouteShortName("Liège-Guillemins - Bruxelles-Central");
        DATA_POINT4.setAgencyId("118");
        DATA_POINT4.setRouteColor("red");
        DATA_POINT4.setRouteDesc("bar");
        DATA_POINT4.setRouteId("515");
        DATA_POINT4.setRouteTextColor("red");
        DATA_POINT4.setRouteType(Route.RouteType.RAIL);
        DATA_POINT4.setRouteUrl(new URL("http://www.github.com"));

    }
}