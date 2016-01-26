package be.raildelays.batch.processor;

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDao;
import org.easymock.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(BlockJUnit4ClassRunner.class)
public class AggregateExpectedTimeProcessorTest extends EasyMockSupport {

    public static final TrainLine TRAIN_LINE = new TrainLine.Builder(0L).build();
    private static final LocalDate TODAY = LocalDate.now();
    private static final Station DEPARTURE_STATION = new Station("Liège-Guillemins");
    private static final Station INTERMEDIATE_STATION = new Station("Bruxelles-Nord");
    private static final Station ARRIVAL_STATION = new Station("Bruxelles-Central");

    @TestSubject
    private AggregateExpectedTimeProcessor processor = new AggregateExpectedTimeProcessor();;
    @Mock(type = MockType.NICE)
    private LineStopDao lineStopDao;
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

    private LineStop item;
    private LineStop expected;

    @Before
    public void setUp() throws ParseException {
        item = new LineStop
                .Builder()
                .date(TODAY)
                .trainLine(TRAIN_LINE)
                .station(INTERMEDIATE_STATION)
                .arrivalTime(TimeDelay.of(LocalTime.parse("18:20"), 0L))
                .departureTime(TimeDelay.of(LocalTime.parse("18:20"), 0L))
                .addNext(new LineStop
                                .Builder()
                                .date(TODAY)
                        .trainLine(TRAIN_LINE)
                                .station(ARRIVAL_STATION)
                                .arrivalTime(TimeDelay.of(null, 0L))
                                .departureTime(TimeDelay.of(null, 0L))
                                .canceledArrival(true)
                )
                .addPrevious(new LineStop
                                .Builder()
                                .date(TODAY)
                        .trainLine(TRAIN_LINE)
                                .station(DEPARTURE_STATION)
                                .arrivalTime(TimeDelay.of(null, 0L))
                                .departureTime(TimeDelay.of(null, 0L))
                )
                .build();

        expected = new LineStop
                .Builder()
                .date(TODAY)
                .trainLine(TRAIN_LINE)
                .station(INTERMEDIATE_STATION)
                .arrivalTime(TimeDelay.of(LocalTime.parse("18:20"), 0L))
                .departureTime(TimeDelay.of(LocalTime.parse("18:21"), 0L))
                .canceledDeparture(true)
                .canceledArrival(true)
                .addNext(new LineStop
                                .Builder()
                                .date(TODAY)
                        .trainLine(new TrainLine.Builder(1L).build()).station(ARRIVAL_STATION)
                                .arrivalTime(TimeDelay.of(LocalTime.parse("18:30"), 0L))
                                .departureTime(TimeDelay.of(LocalTime.parse("18:31"), 0L))
                                .canceledDeparture(true)
                                .canceledArrival(true)
                )
                .addPrevious(new LineStop
                                .Builder()
                                .date(TODAY)
                        .trainLine(new TrainLine.Builder(1L).build()).station(DEPARTURE_STATION)
                                .arrivalTime(TimeDelay.of(LocalTime.parse("17:00"), 0L))
                                .departureTime(TimeDelay.of(LocalTime.parse("17:01"), 0L))
                                .canceledDeparture(true)
                )
                .build();

		/*
         * Note: when we mock the service we must respect order of expectedTime
		 * arrival time.
		 */
    }

    @Test
    public void testProcess() throws Exception {
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, DEPARTURE_STATION))
                .andReturn(expected.getPrevious());
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, INTERMEDIATE_STATION))
                .andReturn(expected);
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, ARRIVAL_STATION))
                .andReturn(expected.getNext());

        replayAll();

        LineStop result = processor.process(item);

        Assert.assertNotNull(result);

        Assert.assertEquals(LocalTime.parse("17:00"), result.getPrevious().getArrivalTime().getExpectedTime());
        Assert.assertEquals(LocalTime.parse("17:01"), result.getPrevious().getDepartureTime().getExpectedTime());
        Assert.assertEquals(LocalTime.parse("18:20"), result.getArrivalTime().getExpectedTime());
        Assert.assertEquals(LocalTime.parse("18:21"), result.getDepartureTime().getExpectedTime());
        Assert.assertEquals(LocalTime.parse("18:30"), result.getNext().getArrivalTime().getExpectedTime());
        Assert.assertEquals(LocalTime.parse("18:31"), result.getNext().getDepartureTime().getExpectedTime());

        verifyAll();
    }

    @Test
    public void testProcessNoCandidate() throws Exception {
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, DEPARTURE_STATION))
                .andReturn(null);
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, INTERMEDIATE_STATION))
                .andReturn(null);
        EasyMock.expect(lineStopDao.findFistScheduledLine(TRAIN_LINE, ARRIVAL_STATION))
                .andReturn(null);

        replayAll();

        LineStop result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(item, result);
    }
}
