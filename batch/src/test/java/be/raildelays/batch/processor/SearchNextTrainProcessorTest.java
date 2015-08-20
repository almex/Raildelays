package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class SearchNextTrainProcessorTest {

    private static final SimpleDateFormat F = new SimpleDateFormat("HH:mm");
    private static final Date TODAY = new Date();
    private static final Station DEPARTURE_STATION = new Station(
            "Liège-Guillemins");
    private static final Station ARRIVAL_STATION = new Station(
            "Bruxelles-Central");
    /**
     * S.U.T.
     */
    private SearchNextTrainProcessor processor;
    private RaildelaysService raildelaysServiceMock;
    private BatchExcelRow item;
    private LineStop stop0;
    private LineStop stop1;
    private List<LineStop> nextLineStops;

    /*
     *        16:30  17:00  17:30  18:00  18:30  19:00
     *          |------|------|------|------|------|
     *      y   |------|
     *      0                 |------|
     *      1          |--------------------|
     */
    @Before
    public void setUp() throws ParseException {
        processor = new SearchNextTrainProcessor();

        raildelaysServiceMock = EasyMock.createMock(RaildelaysService.class);

        processor.setService(raildelaysServiceMock);

        item = new BatchExcelRow.Builder(TODAY, Sens.DEPARTURE) //
                .departureStation(DEPARTURE_STATION) //
                .arrivalStation(ARRIVAL_STATION) //
                .expectedTrain1(new Train("y")) //
                .expectedDepartureTime(F.parse("16:30")) //
                .expectedArrivalTime(F.parse("17:00")) //
                .effectiveTrain1(new Train("y")) //
                .effectiveDepartureTime(F.parse("16:30")) //
                .effectiveArrivalTime(F.parse("17:00")) //
                .canceled(false) //
                .delay(0L) //
                .build();

        stop0 = new LineStop.Builder().date(TODAY)
                .train(new Train("0")).station(ARRIVAL_STATION)
                .arrivalTime(TimeDelay.of(F.parse("18:00"), 0L))
                .departureTime(TimeDelay.of(F.parse("18:00"), 0L))
                .canceled(false)
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(new Train("0")).station(DEPARTURE_STATION)
                        .arrivalTime(TimeDelay.of(F.parse("17:30"), 0L))
                        .departureTime(TimeDelay.of(F.parse("17:30"), 0L))
                        .canceled(false))
                .build();

        stop1 = new LineStop.Builder().date(TODAY)
                .train(new Train("1")).station(ARRIVAL_STATION)
                .arrivalTime(TimeDelay.of(F.parse("18:30"), 0L))
                .departureTime(TimeDelay.of(F.parse("18:30"), 0L))
                .canceled(false)
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(new Train("1")).station(DEPARTURE_STATION)
                        .arrivalTime(TimeDelay.of(F.parse("17:00"), 0L))
                        .departureTime(TimeDelay.of(F.parse("17:00"), 0L))
                        .canceled(false))
                .build();

		/*
         * Note: when we mock the service we must respect order of expectedTime
		 * arrival time.
		 */
        nextLineStops = Arrays.asList(new LineStop[]{stop0, stop1});

    }

    /*
     *        16:30   17:00  17:30  18:00  18:30  19:00
     *          |------|------|------|------|------|
     *      y   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
     *     (0                 |------>>>>>>>|       )
     *      1          |--------------------|
     *
     *     0 should be chosen because :
     *     - the expectedTime arrival time is before 1
     *     - difference between departure delays is less than the
     *     difference between expectedTime arrival time of 0 and 1
     */
    @Test
    public void testTrainIsCanceledAndArrivalDelays() throws Exception {
        stop0 = new LineStop.Builder(stop0)
                .arrivalTime(TimeDelay.of(F.parse("18:00"), 30L))
                .departureTime(TimeDelay.of(F.parse("18:00"), 30L))
                .build();

        nextLineStops = Arrays.asList(new LineStop[]{stop0, stop1});

        item.setCanceled(true);

        EasyMock.expect(
                raildelaysServiceMock.searchNextTrain(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(Date.class))).andReturn(
                nextLineStops);
        EasyMock.replay(raildelaysServiceMock);

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Train("0"), result.getEffectiveTrain1());
        Assert.assertEquals(90, result.getDelay().longValue());

        EasyMock.verify(raildelaysServiceMock);
    }

    /*
     *        16:30   17:00  17:30  18:00  18:30  19:00
	 *          |------|------|------|------|------|
	 *      y   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	 *      0   xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	 *     (1          |--------------------|       )
	 *
	 *     1 should be chosen
	 */
    @Test
    public void testTrainIsMultipleCanceling() throws Exception {
        stop0 = new LineStop.Builder(stop0)
                .canceled(true)
                .build();

        nextLineStops = Arrays.asList(new LineStop[]{stop0, stop1});

        item.setCanceled(true);

        EasyMock.expect(
                raildelaysServiceMock.searchNextTrain(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(Date.class))).andReturn(
                nextLineStops);
        EasyMock.replay(raildelaysServiceMock);

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Train("1"), result.getEffectiveTrain1());
        Assert.assertEquals(90, result.getDelay().longValue());

        EasyMock.verify(raildelaysServiceMock);
    }

    /*
	 *        16:30  17:00  17:30  18:00  18:30  19:00
	 *          |------|------|------|------|------|           
	 *      y   >>>>>>>>>>>>>>>>>>|------>>>>>>>>>>|
	 *     (0                 |------|)
	 *      1          |--------------------|
	 *     
	 *     0 should be chosen because the expectedTime arrival time is before 1
	 */
    @Test
    public void testTrainIsDelayAndNextAreNot() throws Exception {

        item.setEffectiveDepartureTime(F.parse("17:45"));
        item.setEffectiveArrivalTime(F.parse("19:00"));
        item.setDelay(120);

        EasyMock.expect(
                raildelaysServiceMock.searchNextTrain(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(Date.class))).andReturn(
                nextLineStops);
        EasyMock.replay(raildelaysServiceMock);

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Train("0"), result.getEffectiveTrain1());
        Assert.assertEquals(60, result.getDelay().longValue());

        EasyMock.verify(raildelaysServiceMock);
    }

    /*
     *        16:30  17:00  17:30  18:00  18:30  19:00
     *          |------|------|------|------|------|
     *     (y   |------|)
     *      0                 |------|
     *      1          |--------------------|
     *
     *     y should be chosen
     */
    @Test
    public void testTrainIsNotDelay() throws Exception {

        EasyMock.expect(
                raildelaysServiceMock.searchNextTrain(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(Date.class))).andReturn(
                nextLineStops);
        EasyMock.replay(raildelaysServiceMock);

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Train("y"), result.getEffectiveTrain1());
        Assert.assertEquals(0, result.getDelay().longValue());

        EasyMock.verify(raildelaysServiceMock);
    }

    /*
     *        16:30  17:00  17:30  18:00  18:30  19:00
     *          |------|------|------|------|------|
     *     (y   |------>>>>>>>>>>>>>>>>>>>>>|)
     *      0                 |------|
     *      1          |--------------------|
     *
     *     y should be chosen
     */
    @Test
    public void testTrainWithArrivalDelay() throws Exception {

        item.setEffectiveArrivalTime(F.parse("18:30"));
        item.setDelay(90);

        EasyMock.expect(
                raildelaysServiceMock.searchNextTrain(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(Date.class))).andReturn(
                nextLineStops);
        EasyMock.replay(raildelaysServiceMock);

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new Train("y"), result.getEffectiveTrain1());
        Assert.assertEquals(90, result.getDelay().longValue());

        EasyMock.verify(raildelaysServiceMock);
    }
}
