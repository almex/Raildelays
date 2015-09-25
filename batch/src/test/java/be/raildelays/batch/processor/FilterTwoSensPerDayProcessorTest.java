package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.bean.BatchExcelRow.Builder;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private Station stationA;

    private Station stationB;

    @Before
    public void setUp() throws Exception {
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

        list.add(new Builder(LocalDate.parse("2000-01-01"), Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("466")) //
                .expectedArrivalTime(LocalTime.parse("07:00")) //
                .expectedDepartureTime(LocalTime.parse("07:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("07:03")) //
                .effectiveArrivalTime(LocalTime.parse("07:10")) //
                .delay(5L) //
                .index(0L) //
                .build());

        list.add(new Builder(LocalDate.parse("2000-01-01"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("467")) //
                .expectedArrivalTime(LocalTime.parse("15:00")) //
                .expectedDepartureTime(LocalTime.parse("15:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("15:03")) //
                .effectiveArrivalTime(LocalTime.parse("15:10")) //
                .delay(5L) //
                .index(1L) //
                .build());

        list.add(new Builder(LocalDate.parse("2000-01-02"), Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("530")) //
                .expectedArrivalTime(LocalTime.parse("08:00")) //
                .expectedDepartureTime(LocalTime.parse("08:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("08:03")) //
                .effectiveArrivalTime(LocalTime.parse("08:15")) //
                .delay(10L) //
                .index(2L) //
                .build());

        list.add(new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("477")) //
                .expectedArrivalTime(LocalTime.parse("16:00")) //
                .expectedDepartureTime(LocalTime.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(5L) //
                .index(3L) //
                .build());

        list.add(new Builder(LocalDate.parse("2000-01-03"), Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("531")) //
                .expectedArrivalTime(LocalTime.parse("12:00")) //
                .expectedDepartureTime(LocalTime.parse("12:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("12:03")) //
                .effectiveArrivalTime(LocalTime.parse("12:20")) //
                .delay(15L) //
                .index(4L) //
                .build());

        list.add(BatchExcelRow.EMPTY);


        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        processor = new FilterTwoSensPerDayProcessor();
        stepExecution.getExecutionContext().putString("foo", "");
        processor.setOutputReader(fakeReader);
        processor.setLanguage(Language.EN.name());
        processor.afterPropertiesSet();
    }

    @Test
    public void testProcessReturnNew() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(LocalDate.parse("2000-01-05"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("22:00")) //
                .expectedDepartureTime(LocalTime.parse("22:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(5L) //
                .build());

        Assert.assertNotNull(excelRow);
        Assert.assertNull(excelRow.getIndex());
    }

    @Test
    public void testProcessReachEof() throws Exception {
        BatchExcelRow expected = new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("17:00")) //
                .expectedDepartureTime(LocalTime.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(25L) //
                .build();
        BatchExcelRow excelRow = processor.process(expected);

        Assert.assertEquals(expected, excelRow);
    }

    @Test
    public void testProcessReturnReplace() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("17:00")) //
                .expectedDepartureTime(LocalTime.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(25L) //
                .build());

        Assert.assertNotNull(excelRow);
        Assert.assertNotNull(excelRow.getIndex());
    }

    @Test
    public void testProcessReturnSkip() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("16:00")) //
                .expectedDepartureTime(LocalTime.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(0L) //
                .build());

        Assert.assertNull(excelRow);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessNoIndex() throws Exception {
        list.forEach(row -> row.setIndex(null));

        processor.process(new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("17:00")) //
                .expectedDepartureTime(LocalTime.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(25L) //
                .build());
    }

    @Test
    public void testProcessItemStreamException() throws Exception {
        processor.setOutputReader(new AbstractItemStreamItemReader<BatchExcelRow>() {
            @Override
            public BatchExcelRow read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                throw new ItemStreamException("Cannot open the file");
            }
        });

        BatchExcelRow input = new Builder(LocalDate.parse("2000-01-02"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("17:00")) //
                .expectedDepartureTime(LocalTime.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(25L) //
                .build();
        BatchExcelRow output = processor.process(input);

        Assert.assertEquals(input, output);
    }

    @Test
    public void testProcessBefore() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(LocalDate.parse("1999-12-31"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(LocalTime.parse("22:00")) //
                .expectedDepartureTime(LocalTime.parse("22:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(LocalTime.parse("16:03")) //
                .effectiveArrivalTime(LocalTime.parse("16:10")) //
                .delay(5L) //
                .build());

        Assert.assertNotNull(excelRow);
    }

}
