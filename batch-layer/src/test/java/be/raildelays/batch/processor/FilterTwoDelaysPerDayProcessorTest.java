package be.raildelays.batch.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class FilterTwoDelaysPerDayProcessorTest {

	private List<LineStop> fromA;

	private List<LineStop> fromB;

	/**
	 * S.U.T.
	 */
	private FilterTwoDelaysPerDayProcessor processor;

	@Before
	public void setUp() throws ParseException {
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		
		//      1         ->      A          ->      2          ->    B            ->      3
		// 12:00-12:05(5) -> 12:20-12:25(10) -> 12:45-12:50(15) -> 12:55-13:00(20) -> 13:45-<null>(25) 
		LineStop stop1 = new LineStop(today, new Train("466"), new Station(
				"station1"));
		stop1.setArrivalTime(new TimestampDelay(f.parse("12:00"), 5L));
		stop1.setDepartureTime(new TimestampDelay(f.parse("12:05"), 5L));
		LineStop stopA = new LineStop(today, new Train("466"), new Station(
				"stationA"), stop1);
		stopA.setArrivalTime(new TimestampDelay(f.parse("12:20"), 10L));
		stopA.setDepartureTime(new TimestampDelay(f.parse("12:25"), 10L));
		LineStop stop2 = new LineStop(today, new Train("466"), new Station(
				"station2"), stopA);
		stop2.setArrivalTime(new TimestampDelay(f.parse("12:45"), 15L));
		stop2.setDepartureTime(new TimestampDelay(f.parse("12:50"), 15L));
		LineStop stopB = new LineStop(today, new Train("466"), new Station(
				"stationB"), stop2);
		stopB.setArrivalTime(new TimestampDelay(f.parse("12:55"), 20L));
		stopB.setDepartureTime(new TimestampDelay(f.parse("13:00"), 20L));
		LineStop stop3 = new LineStop(today, new Train("466"), new Station(
				"station3"), stopB);
		stop3.setArrivalTime(new TimestampDelay(f.parse("13:45"), 25L));
		stop3.setDepartureTime(null);

		fromA = new ArrayList<>();
		fromA.add(stopA);

		fromB = new ArrayList<>();
		fromB.add(stopB);

		fromA.add(stopA);
		processor = new FilterTwoDelaysPerDayProcessor();
		processor.setStationA("stationA"); 
		processor.setStationB("stationB"); 
		processor.setValidator(factory.getValidator());
	}

	@Test
	public void testProcessFromA() throws Exception {
		List<ExcelRow> excelRows = processor.process(fromA);
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm");

		Assert.assertEquals(1, excelRows.size());	
		ExcelRow excelRow = excelRows.get(0);
		Assert.assertEquals(new Station("stationA"), excelRow.getDepartureStation());
		Assert.assertEquals(new Station("stationB"), excelRow.getArrivalStation());
		Assert.assertEquals(new Train("466"), excelRow.getExpectedTrain1());
		Assert.assertEquals(new Train("466"), excelRow.getEffectiveTrain1());
		Assert.assertEquals(formater.parse("12:25"), excelRow.getExpectedDepartureHour());
		Assert.assertEquals(formater.parse("12:55"), excelRow.getExpectedArrivalHour());
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
