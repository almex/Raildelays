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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.Resource;

import java.text.SimpleDateFormat;
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

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

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
		
		list.add(new Builder(DATE_FORMAT.parse("01/01/2000"), Sens.DEPARTURE) //
                .departureStation(stationA) //
                .arrivalStation(stationB) //
                .expectedTrain1(new Train("466")) //
                .expectedArrivalTime(TIME_FORMAT.parse("07:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("07:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("07:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("07:10")) //
                .delay(5L) //
                .index(0L) //
                .build());

        list.add(new Builder(DATE_FORMAT.parse("01/01/2000"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("467")) //
                .expectedArrivalTime(TIME_FORMAT.parse("15:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("15:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("15:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("15:10")) //
                .delay(5L) //
                .index(1L) //
                .build());

		list.add(new Builder(DATE_FORMAT.parse("02/01/2000"), Sens.DEPARTURE) //
				.departureStation(stationA) //
				.arrivalStation(stationB) //
				.expectedTrain1(new Train("530")) //
				.expectedArrivalTime(TIME_FORMAT.parse("08:00")) //
				.expectedDepartureTime(TIME_FORMAT.parse("08:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(TIME_FORMAT.parse("08:03")) //
				.effectiveArrivalTime(TIME_FORMAT.parse("08:15")) //
				.delay(10L) //
                .index(2L) //
				.build());

        list.add(new Builder(DATE_FORMAT.parse("02/01/2000"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("477")) //
                .expectedArrivalTime(TIME_FORMAT.parse("16:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("16:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("16:10")) //
                .delay(5L) //
                .index(3L) //
                .build());

		list.add(new Builder(DATE_FORMAT.parse("03/01/2000"), Sens.DEPARTURE) //
				.departureStation(stationA) //
				.arrivalStation(stationB) //
				.expectedTrain1(new Train("531")) //
				.expectedArrivalTime(TIME_FORMAT.parse("12:00")) //
				.expectedDepartureTime(TIME_FORMAT.parse("12:05")) //
				.effectiveTrain1(new Train("466")) //
				.effectiveDepartureTime(TIME_FORMAT.parse("12:03")) //
				.effectiveArrivalTime(TIME_FORMAT.parse("12:20")) //
				.delay(15L) //
                .index(4L) //
				.build());


        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
		processor = new FilterTwoSensPerDayProcessor();
        stepExecution.getExecutionContext().putString("foo", "");
        processor.setOutputReader(fakeReader);
        processor.afterPropertiesSet();
	}

	@Test
	public void testProcessReturnNew() throws Exception {
		BatchExcelRow excelRow = processor.process(new Builder(DATE_FORMAT.parse("05/01/2000"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(TIME_FORMAT.parse("22:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("22:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("16:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("16:10")) //
                .delay(5L) //
                .build());

		Assert.assertNotNull(excelRow);
        Assert.assertNull(excelRow.getIndex());
	}
	
	@Test
	public void testProcessReturnReplace() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(DATE_FORMAT.parse("02/01/2000"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(TIME_FORMAT.parse("17:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("17:05")) //
                .effectiveTrain1(new Train("578")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("16:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("16:10")) //
                .delay(25L) //
                .build());

        Assert.assertNotNull(excelRow);
        Assert.assertNotNull(excelRow.getIndex());
	}
	
	@Test
	public void testProcessReturnSkip() throws Exception {
        BatchExcelRow excelRow = processor.process(new Builder(DATE_FORMAT.parse("02/01/2000"), Sens.ARRIVAL) //
                .departureStation(stationB) //
                .arrivalStation(stationA) //
                .expectedTrain1(new Train("578")) //
                .expectedArrivalTime(TIME_FORMAT.parse("16:00")) //
                .expectedDepartureTime(TIME_FORMAT.parse("16:05")) //
                .effectiveTrain1(new Train("466")) //
                .effectiveDepartureTime(TIME_FORMAT.parse("16:03")) //
                .effectiveArrivalTime(TIME_FORMAT.parse("16:10")) //
                .delay(0L) //
                .build());

        Assert.assertNull(excelRow);
	}

}
