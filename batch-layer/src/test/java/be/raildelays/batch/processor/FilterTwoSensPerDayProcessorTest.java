package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.bean.BatchExcelRow.Builder;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RunWith(value = BlockJUnit4ClassRunner.class)
public class FilterTwoSensPerDayProcessorTest {

    private List<BatchExcelRow> list;

    /**
     * S.U.T.
     */
    private FilterTwoSensPerDayProcessor processor;

    private ResourceAwareItemReaderItemStream<BatchExcelRow> fakeReader;

    private static final SimpleDateFormat F = new SimpleDateFormat("HH:mm");

    private Date today;

    private Station stationA;

    private Station stationB;

    @Before
    public void setUp() throws ParseException {
        today = new Date();
        stationA = new Station("A");
        stationB = new Station("B");

        fakeReader = new ResourceAwareItemReaderItemStream<BatchExcelRow>() {
            private Iterator<BatchExcelRow> iterator;

            @Override
            public void setResource(Resource resource) {

            }

            @Override
            public BatchExcelRow read() throws Exception, UnexpectedInputException, org.springframework.batch.item.ParseException, NonTransientResourceException {
                return iterator.hasNext() ? iterator.next() : null;
            }

            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {
                iterator = list.iterator();
            }

            @Override
            public void update(ExecutionContext executionContext) throws ItemStreamException {

            }

            @Override
            public void close() throws ItemStreamException {
                iterator = null;
            }
        };

        list = new ArrayList<>();

        list.add(new Builder(today, Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("466")) //
                .expectedArrivalTime(F.parse("07:00")) //
                .expectedDepartureTime(F.parse("07:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("07:03")) //
                .effectiveArrivalTime(F.parse("07:10")) //
                .delay(5L) //
                .build());

        list.add(new Builder(today, Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("530")) //
                .expectedArrivalTime(F.parse("08:00")) //
                .expectedDepartureTime(F.parse("08:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("08:03")) //
                .effectiveArrivalTime(F.parse("08:15")) //
                .delay(10L) //
                .build());

        list.add(new Builder(today, Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("531")) //
                .expectedArrivalTime(F.parse("12:00")) //
                .expectedDepartureTime(F.parse("12:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("12:03")) //
                .effectiveArrivalTime(F.parse("12:20")) //
                .delay(15L) //
                .build());

        list.add(new Builder(today, Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("467")) //
                .expectedArrivalTime(F.parse("15:00")) //
                .expectedDepartureTime(F.parse("15:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("15:03")) //
                .effectiveArrivalTime(F.parse("15:10")) //
                .delay(5L) //
                .build());

        list.add(new Builder(today, Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("477")) //
                .expectedArrivalTime(F.parse("16:00")) //
                .expectedDepartureTime(F.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("16:03")) //
                .effectiveArrivalTime(F.parse("16:10")) //
                .delay(5L) //
                .build());

        processor = new FilterTwoSensPerDayProcessor();
        processor.setStationA(stationA.getEnglishName());
        processor.setStationB(stationB.getEnglishName());
    }

    @Test
    public void testProcessReturnNew() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(new SimpleDateFormat("dd/MM/yyyy").parse("08/11/1980"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(F.parse("22:00")) //
                .expectedDepartureTime(F.parse("22:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(F.parse("16:03")) //
                .effectiveArrivalTime(F.parse("16:10")) //
                .delay(5L) //
                .build());

        Assert.assertNotNull(excelRow);
        Assert.assertNull(excelRow.getIndex());
    }

    @Test
    public void testProcessReturnReplace() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(today, Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(F.parse("17:00")) //
                .expectedDepartureTime(F.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(F.parse("16:03")) //
                .effectiveArrivalTime(F.parse("16:10")) //
                .delay(25L) //
                .build());

        Assert.assertNotNull(excelRow);
        Assert.assertNotNull(excelRow.getIndex());
    }

    @Test
    public void testProcessReturnSkip() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(today, Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(F.parse("16:00")) //
                .expectedDepartureTime(F.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(F.parse("16:03")) //
                .effectiveArrivalTime(F.parse("16:10")) //
                .delay(0L) //
                .build());


        Assert.assertNull(excelRow);
    }

}
