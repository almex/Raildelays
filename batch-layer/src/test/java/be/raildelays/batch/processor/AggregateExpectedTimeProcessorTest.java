package be.raildelays.batch.processor;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Almex
 */
public class AggregateExpectedTimeProcessorTest {

    public static final Train TRAIN = new Train("0");
    private AggregateExpectedTimeProcessor processor;

    private RaildelaysService raildelaysServiceMock;

    private static final SimpleDateFormat F = new SimpleDateFormat("HH:mm");

    private LineStop item;

    private LineStop expected;

    private List<LineStop> nextLineStops;

    private static final Date TODAY = new Date();

    private static final Station DEPARTURE_STATION = new Station(
            "Liège-Guillemins");

    private static final Station INTERMEDIATE_STATION = new Station(
            "Bruxelles-Nord");

    private static final Station ARRIVAL_STATION = new Station(
            "Bruxelles-Central");

    @Before
    public void setUp() throws ParseException {
        processor = new AggregateExpectedTimeProcessor();

        raildelaysServiceMock = EasyMock.createMock(RaildelaysService.class);

        processor.setService(raildelaysServiceMock);

        item = new LineStop.Builder().date(TODAY)
                .train(TRAIN).station(INTERMEDIATE_STATION)
                .arrivalTime(new TimestampDelay(F.parse("18:20"), 0L))
                .departureTime(new TimestampDelay(F.parse("18:20"), 0L))
                .canceled(true)
                .addNext(new LineStop.Builder().date(TODAY)
                        .train(TRAIN).station(ARRIVAL_STATION)
                        .arrivalTime(new TimestampDelay(null, 0L))
                        .departureTime(new TimestampDelay(null, 0L))
                        .canceled(true))
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(TRAIN).station(DEPARTURE_STATION)
                        .arrivalTime(new TimestampDelay(null, 0L))
                        .departureTime(new TimestampDelay(null, 0L))
                        .canceled(true))
                .build();

        expected = new LineStop.Builder().date(TODAY)
                .train(TRAIN).station(INTERMEDIATE_STATION)
                .arrivalTime(new TimestampDelay(F.parse("18:20"), 0L))
                .departureTime(new TimestampDelay(F.parse("18:21"), 0L))
                .canceled(true)
                .addNext(new LineStop.Builder().date(TODAY)
                        .train(new Train("1")).station(ARRIVAL_STATION)
                        .arrivalTime(new TimestampDelay(F.parse("18:30"), 0L))
                        .departureTime(new TimestampDelay(F.parse("18:31"), 0L))
                        .canceled(false))
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(new Train("1")).station(DEPARTURE_STATION)
                        .arrivalTime(new TimestampDelay(F.parse("17:00"), 0L))
                        .departureTime(new TimestampDelay(F.parse("17:01"), 0L))
                        .canceled(false))
                .build();

		/*
         * Note: when we mock the service we must respect order of expected
		 * arrival time.
		 */
        nextLineStops = Arrays.asList(new LineStop[]{item, expected});

    }

    @Test
    public void testProcess() throws Exception {
        EasyMock.expect(
                raildelaysServiceMock.searchScheduledLine(
                        TRAIN,
                        DEPARTURE_STATION)).andReturn(
                expected.getPrevious());
        EasyMock.expect(
                raildelaysServiceMock.searchScheduledLine(
                        TRAIN,
                        INTERMEDIATE_STATION)).andReturn(
                expected);
        EasyMock.expect(
                raildelaysServiceMock.searchScheduledLine(
                        TRAIN,
                        ARRIVAL_STATION)).andReturn(
                expected.getNext());
        EasyMock.replay(raildelaysServiceMock);

        LineStop result = processor.process(item);

        Assert.assertNotNull(result);

        Assert.assertEquals(F.parse("17:00"), result.getPrevious().getArrivalTime().getExpected());
        Assert.assertEquals(F.parse("17:01"), result.getPrevious().getDepartureTime().getExpected());
        Assert.assertEquals(F.parse("18:20"), result.getArrivalTime().getExpected());
        Assert.assertEquals(F.parse("18:21"), result.getDepartureTime().getExpected());
        Assert.assertEquals(F.parse("18:30"), result.getNext().getArrivalTime().getExpected());
        Assert.assertEquals(F.parse("18:31"), result.getNext().getDepartureTime().getExpected());

        EasyMock.verify(raildelaysServiceMock);
    }
}
