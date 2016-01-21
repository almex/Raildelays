package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.Delays;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class SearchNextTrainLineProcessorTest extends EasyMockSupport {

    public static final String LIEGE_GUILLEMINS = "Liège-Guillemins";
    public static final String BRUXELLES_CENTRAL = "Bruxelles-Central";
    public static final String Y = "416";
    public static final String N0 = "410";
    public static final String N1 = "411";
    private static final LocalDate TODAY = LocalDate.now();
    private static final Station DEPARTURE_STATION = new Station(LIEGE_GUILLEMINS);
    private static final Station ARRIVAL_STATION = new Station(BRUXELLES_CENTRAL);

    @TestSubject
    private SearchNextTrainProcessor processor = new SearchNextTrainProcessor();
    @Mock(type = MockType.NICE)
    private LineStopDao lineStopDao;
    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

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
        item = new BatchExcelRow.Builder(TODAY, Sens.DEPARTURE) //
                .departureStation(DEPARTURE_STATION) //
                .arrivalStation(ARRIVAL_STATION) //
                .expectedTrain1(new TrainLine(Y)) //
                .expectedDepartureTime(LocalTime.parse("16:30")) //
                .expectedArrivalTime(LocalTime.parse("17:00")) //
                .effectiveTrain1(new TrainLine(Y)) //
                .effectiveDepartureTime(LocalTime.parse("16:30")) //
                .effectiveArrivalTime(LocalTime.parse("17:00")) //
                .canceled(false) //
                .delay(0L) //
                .build();

        stop0 = new LineStop.Builder().date(TODAY)
                .train(new TrainLine(N0)).station(ARRIVAL_STATION)
                .arrivalTime(TimeDelay.of(LocalTime.parse("18:00"), 0L))
                .departureTime(TimeDelay.of(LocalTime.parse("18:00"), 0L))
                .canceledArrival(false)
                .canceledDeparture(false)
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(new TrainLine(N0)).station(DEPARTURE_STATION)
                        .arrivalTime(TimeDelay.of(LocalTime.parse("17:30"), 0L))
                        .departureTime(TimeDelay.of(LocalTime.parse("17:30"), 0L))
                        .canceledArrival(false)
                        .canceledDeparture(false))
                .build();

        stop1 = new LineStop.Builder().date(TODAY)
                .train(new TrainLine(N1)).station(ARRIVAL_STATION)
                .arrivalTime(TimeDelay.of(LocalTime.parse("18:30"), 0L))
                .departureTime(TimeDelay.of(LocalTime.parse("18:30"), 0L))
                .canceledArrival(false)
                .canceledDeparture(false)
                .addPrevious(new LineStop.Builder().date(TODAY)
                        .train(new TrainLine(N1)).station(DEPARTURE_STATION)
                        .arrivalTime(TimeDelay.of(LocalTime.parse("17:00"), 0L))
                        .departureTime(TimeDelay.of(LocalTime.parse("17:00"), 0L))
                        .canceledArrival(false)
                        .canceledDeparture(false))
                .build();

		/*
         * Note: when we mock the service we must respect order of expectedTime
		 * arrival time.
		 */
        nextLineStops = Arrays.asList(stop0, stop1);

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
                .arrivalTime(TimeDelay.of(LocalTime.parse("18:00"), Delays.toMillis(30L)))
                .departureTime(TimeDelay.of(LocalTime.parse("18:00"), Delays.toMillis(30L)))
                .build();
        processor.setLanguage(Language.EN.name());

        nextLineStops = Arrays.asList(stop0, stop1);

        item.setCanceled(true);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                EasyMock.anyObject(Station.class),
                EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(N0), result.getEffectiveTrainLine1());
        Assert.assertEquals(90, result.getDelay().longValue());

        verifyAll();
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
        final Language lang = Language.FR;

        stop0 = new LineStop.Builder(stop0, false, false)
                .train(new TrainLine(N0, lang))
                .station(new Station(BRUXELLES_CENTRAL, lang))
                .addPrevious(new LineStop
                        .Builder(stop0.getPrevious(), false, false)
                        .train(new TrainLine(N0, lang))
                        .station(new Station(LIEGE_GUILLEMINS, lang))
                )
                .canceledArrival(true)
                .canceledDeparture(true)
                .build();
        stop1 = new LineStop.Builder(stop1, false, false)
                .train(new TrainLine(N1, lang))
                .station(new Station(BRUXELLES_CENTRAL, lang))
                .addPrevious(new LineStop
                        .Builder(stop1.getPrevious(), false, false)
                        .train(new TrainLine(N1, lang))
                        .station(new Station(LIEGE_GUILLEMINS, lang))
                )
                .build();
        item.setExpectedTrainLine1(new TrainLine(Y, lang));
        item.setEffectiveTrainLine1(new TrainLine(Y, lang));
        item.setDepartureStation(new Station(LIEGE_GUILLEMINS, lang));
        item.setArrivalStation(new Station(BRUXELLES_CENTRAL, lang));
        processor.setLanguage(lang.name());

        nextLineStops = Arrays.asList(stop0, stop1);

        item.setCanceled(true);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(N1, lang), result.getEffectiveTrainLine1());
        Assert.assertEquals(90, result.getDelay().longValue());

        verifyAll();
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
        final Language lang = Language.NL;

        stop0 = new LineStop.Builder(stop0, false, false)
                .train(new TrainLine(N0, lang))
                .station(new Station(BRUXELLES_CENTRAL, lang))
                .addPrevious(new LineStop
                        .Builder(stop0.getPrevious(), false, false)
                        .train(new TrainLine(N0, lang))
                        .station(new Station(LIEGE_GUILLEMINS, lang))
                )
                .build();
        stop1 = new LineStop.Builder(stop1, false, false)
                .train(new TrainLine(N1, lang))
                .station(new Station(BRUXELLES_CENTRAL, lang))
                .addPrevious(new LineStop
                        .Builder(stop1.getPrevious(), false, false)
                        .train(new TrainLine(N1, lang))
                        .station(new Station(LIEGE_GUILLEMINS, lang))
                )
                .build();

        nextLineStops = Arrays.asList(stop0, stop1);

        item.setEffectiveDepartureTime(LocalTime.parse("17:45"));
        item.setEffectiveArrivalTime(LocalTime.parse("19:00"));
        item.setDelay(120);
        item.setExpectedTrainLine1(new TrainLine(Y, lang));
        item.setEffectiveTrainLine1(new TrainLine(Y, lang));
        item.setDepartureStation(new Station(LIEGE_GUILLEMINS, lang));
        item.setArrivalStation(new Station(BRUXELLES_CENTRAL, lang));
        processor.setLanguage(lang.name());

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(N0, lang), result.getEffectiveTrainLine1());
        Assert.assertEquals(60, result.getDelay().longValue());

        verifyAll();
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
        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());
        Assert.assertEquals(0, result.getDelay().longValue());

        verifyAll();
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
        item.setEffectiveArrivalTime(LocalTime.parse("18:30"));
        item.setDelay(90);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());
        Assert.assertEquals(90, result.getDelay().longValue());

        verifyAll();
    }

    @Test
    public void testProcessCollection() throws Exception {
        item.setEffectiveArrivalTime(LocalTime.parse("18:30"));
        item.setDelay(90);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        List<BatchExcelRow> result = processor.process(Collections.singletonList(item));

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.get(0).getEffectiveTrainLine1());
        Assert.assertEquals(90, result.get(0).getDelay().longValue());

        verifyAll();
    }

    @Test
    public void testNoCorrespondingDepartureStation() throws Exception {
        stop0 = new LineStop.Builder(stop0, false, false)
                .station(new Station(BRUXELLES_CENTRAL))
                .addPrevious(new LineStop
                        .Builder(stop0.getPrevious(), false, false)
                        .station(new Station("foo"))
                ).build();
        stop1 = new LineStop.Builder(stop1, false, false)
                .station(new Station(BRUXELLES_CENTRAL))
                .addPrevious(new LineStop
                        .Builder(stop1.getPrevious(), false, false)
                        .station(new Station("foo"))
                ).build();

        nextLineStops = Arrays.asList(stop0, stop1);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());

        verifyAll();
    }

    @Test
    public void testCanceledDeparture() throws Exception {
        stop0 = new LineStop.Builder(stop0, false, false)
                .addPrevious(new LineStop
                        .Builder(stop0.getPrevious(), false, false)
                        .canceledDeparture(true)
                ).build();
        stop1 = new LineStop.Builder(stop1, false, false)
                .addPrevious(new LineStop
                        .Builder(stop1.getPrevious(), false, false)
                        .canceledDeparture(true)
                ).build();

        nextLineStops = Arrays.asList(stop0, stop1);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());

        verifyAll();
    }

    @Test
    public void testCanceledArrival() throws Exception {
        stop0 = new LineStop.Builder(stop0)
                .canceledArrival(true)
                .build();
        stop1 = new LineStop.Builder(stop1)
                .canceledArrival(true)
                .build();

        nextLineStops = Arrays.asList(stop0, stop1);

        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                        EasyMock.anyObject(Station.class),
                        EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(nextLineStops);

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());

        verifyAll();
    }

    @Test
    public void testWithNoResult() throws Exception {
        EasyMock.expect(lineStopDao.findNextExpectedArrivalTime(
                EasyMock.anyObject(Station.class),
                EasyMock.anyObject(LocalDateTime.class)
        )).andReturn(Collections.emptyList());

        replayAll();

        BatchExcelRow result = processor.process(item);

        Assert.assertNotNull(result);
        Assert.assertEquals(new TrainLine(Y), result.getEffectiveTrainLine1());

        verifyAll();
    }
}
