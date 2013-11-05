package be.raildelays.batch.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.service.RaildelaysService;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class SearchNextTrainProcessorTest {

	/**
	 * S.U.T.
	 */
	private SearchNextTrainProcessor processor;

	private RaildelaysService raildelaysServiceMock;

	private static final SimpleDateFormat F = new SimpleDateFormat("HH:mm");

	private BatchExcelRow item;
	
	private List<LineStop> nextLineStops;

	private static final Date TODAY = new Date();

	private static final Station DEPARTURE_STATION = new Station(
			"Liège-Guillemins");

	private static final Station ARRIVAL_STATION = new Station(
			"Bruxelles-Central");
	

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
				.expectedArrivalTime(F.parse("17:30")) //
				.effectiveTrain1(new Train("y")) //
				.effectiveDepartureTime(F.parse("16:30")) //
				.effectiveArrivalTime(F.parse("17:30")) //
				.canceled(false) //
				.delay(0L) //
				.build();
		
		
		LineStop stop10 = new LineStop(TODAY, new Train("0"), DEPARTURE_STATION,
				new TimestampDelay(F.parse("17:30"), 0L),
				new TimestampDelay(F.parse("17:30"), 0L), false);
		LineStop stop20 = new LineStop(TODAY, new Train("0"), ARRIVAL_STATION,
				new TimestampDelay(F.parse("18:00"), 0L),
				new TimestampDelay(F.parse("18:00"), 0L), false, stop10);
		
		LineStop stop11 = new LineStop(TODAY, new Train("1"), DEPARTURE_STATION,
				new TimestampDelay(F.parse("17:00"), 0L),
				new TimestampDelay(F.parse("17:00"), 0L), false);
		LineStop stop21 = new LineStop(TODAY, new Train("1"), ARRIVAL_STATION,
				new TimestampDelay(F.parse("18:30"), 0L),
				new TimestampDelay(F.parse("18:30"), 0L), false, stop11);
		
		/*
		 * Note: when we mock the service we must respect order of expected 
		 * arrival time. 
		 */
		nextLineStops = Arrays.asList(new LineStop[] {stop20, stop21});
		
		
	}

	/*
	 *        16:30   17:00  17:30  18:00  18:30  19:00
	 *          |------|------|------|------|------|           
	 *      y   >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 *     (0                 |------>>>>>>>|)
	 *      1          |--------------------|
	 *     
	 *     0 should be chosen because :
	 *     - the expected arrival time is before 1
	 *     - difference between departure delays is less than the 
	 *     difference between expected arrival time of 0 and 1
	 */
	@Test
	public void testTrainIsCanceledAndArrivalDelays() throws Exception {		
		LineStop stop10 = new LineStop(TODAY, new Train("0"), DEPARTURE_STATION,
				new TimestampDelay(F.parse("17:30"), 0L),
				new TimestampDelay(F.parse("17:30"), 0L), false);
		LineStop stop20 = new LineStop(TODAY, new Train("0"), ARRIVAL_STATION,
				new TimestampDelay(F.parse("18:00"), 30L),
				new TimestampDelay(F.parse("18:00"), 30L), false, stop10);
		
		LineStop stop11 = new LineStop(TODAY, new Train("1"), DEPARTURE_STATION,
				new TimestampDelay(F.parse("17:00"), 0L),
				new TimestampDelay(F.parse("17:00"), 0L), false);
		LineStop stop21 = new LineStop(TODAY, new Train("1"), ARRIVAL_STATION,
				new TimestampDelay(F.parse("18:30"), 0L),
				new TimestampDelay(F.parse("18:30"), 0L), false, stop11);
		
		nextLineStops = Arrays.asList(new LineStop[] {stop20, stop21});

		item.setCanceled(true);
		
		EasyMock.expect(
				raildelaysServiceMock.searchNextTrain(
						EasyMock.anyObject(Station.class),
						EasyMock.anyObject(Date.class))).andReturn(nextLineStops);
		EasyMock.replay(raildelaysServiceMock);

		BatchExcelRow result = processor.process(item);

		Assert.assertNotNull(result);
		Assert.assertEquals(new Train("0"), result.getEffectiveTrain1());

		EasyMock.verify(raildelaysServiceMock);
	}
	
	/*
	 *        16:30  17:00  17:30  18:00  18:30  19:00
	 *          |------|------|------|------|------|           
	 *      y   >>>>>>>>>>>>>>|------>>>>>>>>>>>>>>|
	 *     (0                 |------|)
	 *      1          |--------------------|
	 *     
	 *     0 should be chosen because the expected arrival time is before 1
	 */
	@Test
	public void testTrainIsDelayAndNextAreNot() throws Exception {
		
		item.setEffectiveDepartureTime(F.parse("17:30"));
		item.setEffectiveArrivalTime(F.parse("19:00"));
		item.setDelay(120);

		EasyMock.expect(
				raildelaysServiceMock.searchNextTrain(
						EasyMock.anyObject(Station.class),
						EasyMock.anyObject(Date.class))).andReturn(nextLineStops);
		EasyMock.replay(raildelaysServiceMock);

		BatchExcelRow result = processor.process(item);

		Assert.assertNotNull(result);
		Assert.assertEquals(new Train("0"), result.getEffectiveTrain1());

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
						EasyMock.anyObject(Date.class))).andReturn(nextLineStops);
		EasyMock.replay(raildelaysServiceMock);

		BatchExcelRow result = processor.process(item);

		Assert.assertNotNull(result);
		Assert.assertEquals(new Train("y"), result.getEffectiveTrain1());

		EasyMock.verify(raildelaysServiceMock);
	}
}
