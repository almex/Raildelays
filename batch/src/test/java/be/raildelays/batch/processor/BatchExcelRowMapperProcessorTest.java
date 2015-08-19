package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.delays.TimestampDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class BatchExcelRowMapperProcessorTest {

    private LineStop fromA;

    private LineStop fromB;

    /**
     * S.U.T.
     */
    private BatchExcelRowMapperProcessor processor;

    @Before
    public void setUp() throws ParseException {
        Date today = new Date();
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");

        // 1 -> A -> 2 -> B -> 3
        // 12:00-12:05(5) -> 12:20-12:25(10) -> 12:45-12:50(15) ->
        // 12:55-13:00(20) -> 13:45-<null>(25)
        TimestampDelay arrivalTime;
        TimestampDelay departureTime;

		/*arrivalTime = TimestampDelay.of(f.parse("12:00"), 5L);
        departureTime = TimestampDelay.of(f.parse("12:05"), 5L);
		LineStop stop1 = new LineStop.Builder().date(today)
				.train(new Train("466")).station(new Station("station1"))
				.arrivalTime(arrivalTime).departureTime(departureTime)
				.canceled(false).build();
		arrivalTime = TimestampDelay.of(f.parse("12:20"), 10L);
		departureTime = TimestampDelay.of(f.parse("12:25"), 10L);
		LineStop stopA = new LineStop.Builder().date(today)
				.train(new Train("466")).station(new Station("stationA"))
				.arrivalTime(arrivalTime).departureTime(departureTime)
				.canceled(false).addPrevious(stop1).build();
		arrivalTime = TimestampDelay.of(f.parse("12:45"), 15L);
		departureTime = TimestampDelay.of(f.parse("12:50"), 15L);
		LineStop stop2 = new LineStop.Builder().date(today)
				.train(new Train("466")).station(new Station("station2"))
				.arrivalTime(arrivalTime).departureTime(departureTime)
				.canceled(false).addPrevious(stopA).build();
		arrivalTime = TimestampDelay.of(f.parse("12:55"), 20L);
		departureTime = TimestampDelay.of(f.parse("13:00"), 20L);
		LineStop stopB = new LineStop.Builder().date(today)
				.train(new Train("466")).station(new Station("stationB"))
				.arrivalTime(arrivalTime).departureTime(departureTime)
				.canceled(false).addPrevious(stop2).build();
		arrivalTime = TimestampDelay.of(f.parse("13:45"), 25L);
		departureTime = null;
		new LineStop.Builder().date(today).train(new Train("466"))
				.station(new Station("station3")).arrivalTime(arrivalTime)
				.departureTime(departureTime).canceled(false)
				.addPrevious(stopB).build();*/
        arrivalTime = TimestampDelay.of(f.parse("12:00"), 5L);
        departureTime = TimestampDelay.of(f.parse("12:05"), 5L);
        LineStop.Builder builder = new LineStop.Builder().date(today)
                .train(new Train("466")).station(new Station("station1"))
                .arrivalTime(arrivalTime).departureTime(departureTime)
                .canceled(false);
        arrivalTime = TimestampDelay.of(f.parse("12:20"), 10L);
        departureTime = TimestampDelay.of(f.parse("12:25"), 10L);
        builder.addNext(new LineStop.Builder().date(today)
                .train(new Train("466")).station(new Station("stationA"))
                .arrivalTime(arrivalTime).departureTime(departureTime)
                .canceled(false));
        arrivalTime = TimestampDelay.of(f.parse("12:45"), 15L);
        departureTime = TimestampDelay.of(f.parse("12:50"), 15L);
        builder.addNext(new LineStop.Builder().date(today)
                .train(new Train("466")).station(new Station("station2"))
                .arrivalTime(arrivalTime).departureTime(departureTime)
                .canceled(false));
        arrivalTime = TimestampDelay.of(f.parse("12:55"), 20L);
        departureTime = TimestampDelay.of(f.parse("13:00"), 20L);
        builder.addNext(new LineStop.Builder().date(today)
                .train(new Train("466")).station(new Station("stationB"))
                .arrivalTime(arrivalTime).departureTime(departureTime)
                .canceled(false));
        arrivalTime = TimestampDelay.of(f.parse("13:45"), 25L);
        departureTime = null;
        builder.addNext(new LineStop.Builder().date(today).train(new Train("466"))
                .station(new Station("station3")).arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceled(false));

        LineStop lineStop = builder.build();

        fromA = lineStop.getNext();  // stationA

        fromB = lineStop.getNext().getNext().getNext(); // stationB

        processor = new BatchExcelRowMapperProcessor();
        processor.setStationA("stationA");
        processor.setStationB("stationB");
        processor.setLanguage(Language.EN.name());
    }

    @Test
    public void testProcessFromA() throws Exception {
        BatchExcelRow excelRow = processor.process(fromA);
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm");

        Assert.assertEquals(new Station("stationA"),
                excelRow.getDepartureStation());
        Assert.assertEquals(new Station("stationB"),
                excelRow.getArrivalStation());
        Assert.assertEquals(new Train("466"), excelRow.getExpectedTrain1());
        Assert.assertEquals(new Train("466"), excelRow.getEffectiveTrain1());
        Assert.assertEquals(formater.parse("12:25"),
                excelRow.getExpectedDepartureTime());
        Assert.assertEquals(formater.parse("12:55"),
                excelRow.getExpectedArrivalTime());
        Assert.assertEquals(formater.parse("12:35"),
                excelRow.getEffectiveDepartureTime());
        Assert.assertEquals(formater.parse("13:15"),
                excelRow.getEffectiveArrivalTime());
        Assert.assertEquals(20, excelRow.getDelay().longValue());
    }

    @Test
    public void testProcessFromB() throws Exception {
        BatchExcelRow excelRow = processor.process(fromB);

        Assert.assertEquals(new Station("stationA"),
                excelRow.getDepartureStation());
        Assert.assertEquals(new Station("stationB"),
                excelRow.getArrivalStation());
        Assert.assertEquals(20, excelRow.getDelay().longValue());
    }

}
