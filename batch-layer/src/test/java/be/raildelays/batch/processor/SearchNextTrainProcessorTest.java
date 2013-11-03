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

	private static final Date TODAY = new Date();

	private static final Station DEPARTURE_STATION = new Station(
			"Liège-Guillemins");

	private static final Station ARRIVAL_STATION = new Station(
			"Bruxelles-Central");

	@Before
	public void setUp() throws ParseException {
		processor = new SearchNextTrainProcessor();

		raildelaysServiceMock = EasyMock.createMock(RaildelaysService.class);

		processor.setService(raildelaysServiceMock);

		item = new BatchExcelRow.Builder(TODAY, Sens.DEPARTURE) //
				.departureStation(DEPARTURE_STATION) //
				.arrivalStation(ARRIVAL_STATION) //
				.expectedTrain1(new Train("466")) //
				.expectedDepartureTime(F.parse("16:28")) //
				.expectedArrivalTime(F.parse("17:22")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(F.parse("16:28")) //
				.effectiveArrivalTime(F.parse("17:57")) //
				.canceled(true) //
				.delay(25L) //
				.build();
	}

	@Test
	public void testHappyPath() throws Exception {

		List<LineStop> nextLineStops = Arrays.asList(new LineStop[] {
				new LineStop(TODAY, new Train("515"), ARRIVAL_STATION,
						new TimestampDelay(F.parse("16:35"), 0L),
						new TimestampDelay(F.parse("17:45"), 15L), false),
				new LineStop(TODAY, new Train("476"), ARRIVAL_STATION,
						new TimestampDelay(F.parse("17:01"), 0L),
						new TimestampDelay(F.parse("17:55"), 0L), false) });

		EasyMock.expect(
				raildelaysServiceMock.searchNextTrain(
						EasyMock.anyObject(Station.class),
						EasyMock.anyObject(Date.class))).andReturn(nextLineStops);
		EasyMock.replay(raildelaysServiceMock);

		BatchExcelRow result = processor.process(item);

		Assert.assertNotNull(result);
		Assert.assertEquals(new Train("476"), result.getEffectiveTrain1());

		EasyMock.verify(raildelaysServiceMock);
	}
}
