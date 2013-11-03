package be.raildelays.batch.processor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.domain.xls.ExcelRow.Builder;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class FilterTwoSensPerDayProcessorTest {

	private List<ExcelRow> list;

	/**
	 * S.U.T.
	 */
	private FilterTwoSensPerDayProcessor processor;

	@Before
	public void setUp() throws ParseException {
		Date today = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		Station stationA = new Station("A");
		Station stationB = new Station("B");

		list = new ArrayList<>();
		
		list.add(new Builder(today, Sens.DEPARTURE) //
				.departureStation(stationA) //
				.arrivalStation(stationB) //
				.expectedTrain1(new Train("466")) //
				.expectedArrivalTime(f.parse("07:00")) //
				.expectedDepartureTime(f.parse("07:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(f.parse("07:03")) //
				.effectiveArrivalTime(f.parse("07:10")) //
				.delay(5L) //
				.build());

		list.add(new Builder(today, Sens.DEPARTURE) //
				.departureStation(stationA) //
				.arrivalStation(stationB) //
				.expectedTrain1(new Train("530")) //
				.expectedArrivalTime(f.parse("08:00")) //
				.expectedDepartureTime(f.parse("08:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(f.parse("08:03")) //
				.effectiveArrivalTime(f.parse("08:15")) //
				.delay(10L) //
				.build());

		list.add(new Builder(today, Sens.DEPARTURE) //
				.departureStation(stationA) //
				.arrivalStation(stationB) //
				.expectedTrain1(new Train("531")) //
				.expectedArrivalTime(f.parse("12:00")) //
				.expectedDepartureTime(f.parse("12:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(f.parse("12:03")) //
				.effectiveArrivalTime(f.parse("12:20")) //
				.delay(15L) //
				.build());

		list.add(new Builder(today, Sens.ARRIVAL) //
				.departureStation(stationB) //
				.arrivalStation(stationA) //
				.expectedTrain1(new Train("467")) //
				.expectedArrivalTime(f.parse("15:00")) //
				.expectedDepartureTime(f.parse("15:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(f.parse("15:03")) //
				.effectiveArrivalTime(f.parse("15:10")) //
				.delay(5L) //
				.build());

		list.add(new Builder(today, Sens.ARRIVAL) //
				.departureStation(stationB) //
				.arrivalStation(stationA) //
				.expectedTrain1(new Train("477")) //
				.expectedArrivalTime(f.parse("16:00")) //
				.expectedDepartureTime(f.parse("16:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(f.parse("16:03")) //
				.effectiveArrivalTime(f.parse("16:10")) //
				.delay(5L) //
				.build());

		processor = new FilterTwoSensPerDayProcessor();
		processor.setStationA(stationA.getEnglishName());
		processor.setStationB(stationB.getEnglishName());
	}

	@Test
	public void testProcessReturnTwo() throws Exception {
		List<ExcelRow> excelRows = processor.process(list);

		Assert.assertEquals(2, excelRows.size());
	}
	
	@Test
	public void testProcessOrder() throws Exception {
		List<ExcelRow> excelRows = processor.process(list);

		ExcelRow excelRow1 = excelRows.get(0);
		ExcelRow excelRow2 = excelRows.get(1);
		
		Assert.assertEquals(Sens.DEPARTURE, excelRow1.getSens());
		Assert.assertEquals(Sens.ARRIVAL, excelRow2.getSens());
	}
	
	@Test
	public void testProcessMaxDelaysPerSens() throws Exception {
		List<ExcelRow> excelRows = processor.process(list);

		ExcelRow excelRow1 = excelRows.get(0);
		ExcelRow excelRow2 = excelRows.get(1);
		
		Assert.assertEquals(15, excelRow1.getDelay());
		Assert.assertEquals(5, excelRow2.getDelay());
	}
	
	@Test
	public void testProcessTime() throws Exception {
		SimpleDateFormat f = new SimpleDateFormat("HH:mm");
		List<ExcelRow> excelRows = processor.process(list);

		ExcelRow excelRow1 = excelRows.get(0);
		ExcelRow excelRow2 = excelRows.get(1);
		
		Assert.assertEquals(f.parse("12:00"), excelRow1.getExpectedArrivalTime());
		Assert.assertEquals("The first element with the max delay should be the one we keep", f.parse("15:10"), excelRow2.getEffectiveArrivalHour());
	}

}
