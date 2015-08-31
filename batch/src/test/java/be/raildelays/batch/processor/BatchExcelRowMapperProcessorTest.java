package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.exception.ArrivalDepartureEqualsException;
import be.raildelays.delays.Delays;
import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class BatchExcelRowMapperProcessorTest {

    private LineStop fromA;

    private LineStop fromB;

    /**
     * S.U.T.
     */
    private BatchExcelRowMapperProcessor processor;

    @Before
    public void setUp() throws Exception {
        LocalDate today = LocalDate.now();

        // 1 -> A -> 2 -> B -> 3
        // 12:00-12:05(5) -> 12:20-12:25(10) -> 12:45-12:50(15) ->
        // 12:55-13:00(20) -> 13:45-<null>(25)
        TimeDelay arrivalTime;
        TimeDelay departureTime;

        arrivalTime = TimeDelay.of(LocalTime.parse("12:00"), Delays.toMillis(5L));
        departureTime = TimeDelay.of(LocalTime.parse("12:05"), Delays.toMillis(5L));
        LineStop.Builder builder = new LineStop
                .Builder()
                .date(today)
                .train(new Train("466"))
                .station(new Station("station1"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledDeparture(false)
                .canceledArrival(false);
        arrivalTime = TimeDelay.of(LocalTime.parse("12:20"), Delays.toMillis(10L));
        departureTime = TimeDelay.of(LocalTime.parse("12:25"), Delays.toMillis(10L));
        builder.addNext(new LineStop
                .Builder()
                .date(today)
                .train(new Train("466"))
                .station(new Station("stationA"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledDeparture(false)
                .canceledArrival(false));
        arrivalTime = TimeDelay.of(LocalTime.parse("12:45"), Delays.toMillis(15L));
        departureTime = TimeDelay.of(LocalTime.parse("12:50"), Delays.toMillis(15L));
        builder.addNext(new LineStop
                .Builder()
                .date(today)
                .train(new Train("466"))
                .station(new Station("station2"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledDeparture(false)
                .canceledArrival(false));
        arrivalTime = TimeDelay.of(LocalTime.parse("12:55"), Delays.toMillis(20L));
        departureTime = TimeDelay.of(LocalTime.parse("13:00"), Delays.toMillis(20L));
        builder.addNext(new LineStop
                .Builder()
                .date(today)
                .train(new Train("466"))
                .station(new Station("stationB"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledDeparture(false)
                .canceledArrival(false));
        arrivalTime = TimeDelay.of(LocalTime.parse("13:45"), Delays.toMillis(25L));
        departureTime = null;
        builder.addNext(new LineStop
                .Builder()
                .date(today)
                .train(new Train("466"))
                .station(new Station("station3"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledDeparture(false)
                .canceledArrival(false));

        LineStop lineStop = builder.build();

        fromA = lineStop.getNext();  // stationA

        fromB = lineStop.getNext().getNext().getNext(); // stationB

        processor = new BatchExcelRowMapperProcessor();
        processor.setStationA("stationA");
        processor.setStationB("stationB");
        processor.setLanguage(Language.EN.name());
        processor.afterPropertiesSet();
    }

    @Test
    public void testProcessFromA() throws Exception {
        BatchExcelRow excelRow = processor.process(fromA);

        Assert.assertEquals(new Station("stationA"), excelRow.getDepartureStation());
        Assert.assertEquals(new Station("stationB"), excelRow.getArrivalStation());
        Assert.assertEquals(new Train("466"), excelRow.getExpectedTrain1());
        Assert.assertEquals(new Train("466"), excelRow.getEffectiveTrain1());
        Assert.assertEquals(LocalTime.parse("12:25"), excelRow.getExpectedDepartureTime());
        Assert.assertEquals(LocalTime.parse("12:55"), excelRow.getExpectedArrivalTime());
        Assert.assertEquals(LocalTime.parse("12:35"), excelRow.getEffectiveDepartureTime());
        Assert.assertEquals(LocalTime.parse("13:15"), excelRow.getEffectiveArrivalTime());
        Assert.assertEquals(20 * 60 * 1000, excelRow.getDelay().longValue());
    }

    @Test
    public void testProcessFromB() throws Exception {
        BatchExcelRow excelRow = processor.process(fromB);

        Assert.assertEquals(new Station("stationA"), excelRow.getDepartureStation());
        Assert.assertEquals(new Station("stationB"), excelRow.getArrivalStation());
        Assert.assertEquals(Delays.toMillis(20L), excelRow.getDelay());
    }

    @Test
    public void testProcessSensDeparture() throws Exception {
        BatchExcelRow excelRow = processor.process(fromB);

        Assert.assertEquals(Sens.DEPARTURE, excelRow.getSens());
    }

    @Test
    public void testProcessSensArrival() throws Exception {
        processor.setStationA("stationB");
        processor.setStationB("stationA");

        BatchExcelRow excelRow = processor.process(fromB);

        Assert.assertEquals(Sens.ARRIVAL, excelRow.getSens());
    }

    @Test(expected = ArrivalDepartureEqualsException.class)
    public void testArrivalDepartureEquals() throws Exception {
        // We create a LineStop with no departure/arrival station
        LineStop actual = new LineStop
                .Builder(fromA, false, false)
                .station(new Station("foo"))
                .build();

        processor.process(actual);
    }

}
