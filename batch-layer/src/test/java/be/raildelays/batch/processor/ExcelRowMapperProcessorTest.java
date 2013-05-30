package be.raildelays.batch.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class ExcelRowMapperProcessorTest {

	private List<LineStop> fromA;

	private List<LineStop> fromB;

	/**
	 * S.U.T.
	 */
	private ExcelRowMapperProcessor processor;

	@Before
	public void setUp() throws ParseException {
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		
		//      1         ->      A          ->      2          ->    B            ->      3
		// 12:00-12:05(5) -> 12:20-12:25(10) -> 12:45-12:50(15) -> 12:55-13:00(20) -> 13:45-<null>(25)
		TimestampDelay arrivalTime;
		TimestampDelay departureTime;
		
		arrivalTime = new TimestampDelay(f.parse("12:00"), 5L);
		departureTime = new TimestampDelay(f.parse("12:05"), 5L);		
		LineStop stop1 = new LineStop(today, new Train("466"), new Station(
				"station1"), arrivalTime, departureTime, false);
		arrivalTime = new TimestampDelay(f.parse("12:20"), 10L);
		departureTime = new TimestampDelay(f.parse("12:25"), 10L);
		LineStop stopA = new LineStop(today, new Train("466"), new Station(
				"stationA"), arrivalTime, departureTime, false, stop1);
		arrivalTime = new TimestampDelay(f.parse("12:45"), 15L);
		departureTime = new TimestampDelay(f.parse("12:50"), 15L);
		LineStop stop2 = new LineStop(today, new Train("466"), new Station(
				"station2"), arrivalTime, departureTime, false, stopA);
		arrivalTime = new TimestampDelay(f.parse("12:55"), 20L);
		departureTime = new TimestampDelay(f.parse("13:00"), 20L);
		LineStop stopB = new LineStop(today, new Train("466"), new Station(
				"stationB"), arrivalTime, departureTime, false, stop2);
		arrivalTime = new TimestampDelay(f.parse("13:45"), 25L);
		departureTime = null;
		new LineStop(today, new Train("466"), new Station(
				"station3"), arrivalTime, departureTime, false, stopB);

		fromA = new ArrayList<>();
		fromA.add(stopA);

		fromB = new ArrayList<>();
		fromB.add(stopB);

		fromA.add(stopA);
		processor = new ExcelRowMapperProcessor();
		processor.setStationA("stationA"); 
		processor.setStationB("stationB"); 
	}

	@Test
	@Ignore
	public void testProcessFromA() throws Exception {
		List<ExcelRow> excelRows = processor.process(fromA);
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm");

		Assert.assertEquals(1, excelRows.size());	
		ExcelRow excelRow = excelRows.get(0);
		Assert.assertEquals(new Station("stationA"), excelRow.getDepartureStation());
		Assert.assertEquals(new Station("stationB"), excelRow.getArrivalStation());
		Assert.assertEquals(new Train("466"), excelRow.getExpectedTrain1());
		Assert.assertEquals(new Train("466"), excelRow.getEffectiveTrain1());
		Assert.assertEquals(formater.parse("12:25"), excelRow.getExpectedDepartureTime());
		Assert.assertEquals(formater.parse("12:55"), excelRow.getExpectedArrivalTime());
		Assert.assertEquals(formater.parse("12:35"), excelRow.getEffectiveDepartureHour());
		Assert.assertEquals(formater.parse("13:15"), excelRow.getEffectiveArrivalHour());
		Assert.assertEquals(20, excelRow.getDelay());
	}

	@Test
	public void testProcessFromB() throws Exception {
		List<ExcelRow> excelRows = processor.process(fromB);

		Assert.assertEquals(1, excelRows.size());
		ExcelRow excelRow = excelRows.get(0);
		Assert.assertEquals(new Station("stationA"), excelRow.getDepartureStation());
		Assert.assertEquals(new Station("stationB"), excelRow.getArrivalStation());
		Assert.assertEquals(20, excelRow.getDelay());
	}

}
