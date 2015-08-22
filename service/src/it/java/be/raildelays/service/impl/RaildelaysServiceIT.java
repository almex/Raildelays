package be.raildelays.service.impl;

import be.raildelays.domain.Language;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.Station;
import be.raildelays.service.RaildelaysService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/service/raildelays-service-integration-test-context.xml"})
@Transactional
@Rollback
public class RaildelaysServiceIT {

    /**
     * SUT.
     */
    @Resource
    RaildelaysService raildelaysService;

    @Test
    public void test466LineStop() throws ParseException {
        LocalDate today = LocalDate.now();
        RouteLogDTO routeLog = new RouteLogDTO("466", today, Language.EN);
        ServedStopDTO stop = new ServedStopDTO("Liège-Guillemins",
                LocalTime.parse("16:00"), 5, LocalTime.parse("17:00"), 15, false);

        routeLog.addStop(stop);

        Assert.assertNotNull(raildelaysService.saveRouteLog(routeLog));
    }

    @Test
    public void testLinkedLineStop() throws ParseException {
        LocalDate today = LocalDate.now();
        RouteLogDTO routeLog = new RouteLogDTO("466", today, Language.EN);
        routeLog.addStop(new ServedStopDTO("Liège-Guillemins", LocalTime
                .parse("06:58"), 5, LocalTime.parse("07:05"), 5, false));
        routeLog.addStop(new ServedStopDTO("Leuven", LocalTime.parse("07:42"),
                9, LocalTime.parse("07:53"), 15, false));
        routeLog.addStop(new ServedStopDTO("Bruxelles-Central", LocalTime
                .parse("08:20"), 20, LocalTime.parse("08:25"), 20, false));

        Assert.assertNotNull(raildelaysService.saveRouteLog(routeLog));
    }

    @Test
    public void testSearchLineStop() throws ParseException {
        LocalDate today = LocalDate.now();
        RouteLogDTO routeLog = new RouteLogDTO("466", today, Language.EN);
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
        routeLog.addStop(new ServedStopDTO("Liège-Guillemins", LocalTime
                .parse("06:58"), 5, LocalTime.parse("07:05"), 5, false));
        routeLog.addStop(new ServedStopDTO("Leuven", LocalTime.parse("07:42"),
                9, LocalTime.parse("07:53"), 15, false));
        routeLog.addStop(new ServedStopDTO("Bruxelles-Central", LocalTime
                .parse("08:20"), 20, LocalTime.parse("08:25"), 20, false));

        Assert.assertEquals(3, raildelaysService.saveRouteLog(routeLog).size());
        Assert.assertEquals(
                2,
                raildelaysService.searchDelaysBetween(today,
                        new Station("Liège-Guillemins"),
                        new Station("Bruxelles-Central"), 1).size());
    }

}
