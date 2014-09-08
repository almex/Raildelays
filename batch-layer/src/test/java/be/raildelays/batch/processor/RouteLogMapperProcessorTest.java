package be.raildelays.batch.processor;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.railtime.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class RouteLogMapperProcessorTest {

    private TwoDirections directions;

    /**
     * S.U.T.
     */
    private RouteLogMapperProcessor processor;

    @Before
    public void setUp() throws ParseException {
        Station stationA = new Station("A");
        Station stationB = new Station("B");
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");

        //      1         ->      A          ->      2
        // 12:00-12:00 -> 12:20-12:25(10) -> 12:45-12:50(15)

        // ... ->      B          ->      3
        // ... -> 12:55-13:00(20) -> 13:45-13:45

        Direction direction1 = new Direction(new Train("466"));

        direction1.setFrom(stationA);
        direction1.setTo(stationB);
        direction1.setLibelle("From A to B");
        direction1.setSteps(Arrays.asList(
                new Step(0, "1", f.parse("12:00"), 0L, false), //
                new Step(1, "A", f.parse("12:20"), 10L, false), //
                new Step(2, "2", f.parse("12:45"), 15L, false), //
                new Step(3, "B", f.parse("12:55"), 20L, false), //
                new Step(4, "3", f.parse("13:45"), 0L, true)));

        Direction direction2 = new Direction(new Train("466"));

        direction1.setFrom(stationA);
        direction1.setTo(stationB);
        direction2.setLibelle("From A to B");
        direction2.setSteps(Arrays.asList(
                new Step(0, "1", f.parse("12:00"), 0L, false), //
                new Step(1, "A", f.parse("12:25"), 10L, false), //
                new Step(2, "2", f.parse("12:50"), 15L, false), //
                new Step(3, "B", f.parse("13:00"), 20L, false), //
                new Step(4, "3", f.parse("13:45"), 0L, true)));

        directions = new TwoDirections(direction1, direction2);

        processor = new RouteLogMapperProcessor();

        processor.setDate(new Date());
    }

    @Test
    public void testNumberOfStops() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);

        Assert.assertEquals(5, routeLog.getStops().size());
    }

    @Test
    public void testDate() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);

        Assert.assertEquals(processor.getDate(), routeLog.getDate());
    }

    @Test
    public void testTrainId() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);

        Assert.assertEquals("466", routeLog.getTrainId());
    }

    @Test
    public void testArrivalTimeGreaterThanDepartureTime() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);

        for (ServedStopDTO stop : routeLog.getStops()) {
            Assert.assertThat(stop.getArrivalTime(), greaterThanOrEqualTo(stop.getDepartureTime()));
        }
    }

    @Test
    public void testCanceled() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);
        ServedStopDTO stop = routeLog.getStops().get(4);

        Assert.assertTrue(stop.isCanceled());
    }

    @Test
    public void testEmpty() throws Exception {
        RouteLogDTO routeLog = processor.process(new TwoDirections(null, null));

        Assert.assertNull("An empty direction should return a null value as the end of the process", routeLog);
    }

    @Test
    public void testOrder() throws Exception {
        RouteLogDTO routeLog = processor.process(directions);

        for (int i = 0; i < routeLog.getStops().size(); i++) {
            ServedStopDTO stop = routeLog.getStops().get(i);

            switch (i) {
                case 0:
                    Assert.assertEquals("1", stop.getStationName());
                    break;
                case 1:
                    Assert.assertEquals("A", stop.getStationName());
                    break;
                case 2:
                    Assert.assertEquals("2", stop.getStationName());
                    break;
                case 3:
                    Assert.assertEquals("B", stop.getStationName());
                    break;
                case 4:
                    Assert.assertEquals("3", stop.getStationName());
                    break;
            }
        }
    }

}
