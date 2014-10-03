package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class BatchExcelRowInContextReaderTest {

    private static String KEY_NAME = "foo";
    private BatchExcelRowInContextReader reader;
    private Date now;
    private ExecutionContext context;
    private BatchExcelRow expected;

    @Before
    public void setUp() throws Exception {
        final DateFormat timeFormat = new SimpleDateFormat("HH:mm");

        now = new Date();
        context = MetaDataInstanceFactory.createStepExecution()
                .getExecutionContext();
        reader = new BatchExcelRowInContextReader();
        expected = new BatchExcelRow.Builder(now, Sens.DEPARTURE)
                .departureStation(new Station("BRUXELLES-CENTRAL")) //
                .arrivalStation(new Station("LIEGE-GUILLEMINS")) //
                .expectedDepartureTime(timeFormat.parse("14:00")) //
                .expectedArrivalTime(timeFormat.parse("15:00")) //
                .expectedTrain1(new Train("529")) //
                .expectedTrain2(new Train("516")) //
                .effectiveDepartureTime(timeFormat.parse("14:05")) //
                .effectiveArrivalTime(timeFormat.parse("15:15")) //
                .effectiveTrain1(new Train("529")) //
                .effectiveTrain2(new Train("516")) //
                .delay(10L) //
                .build(false);
        reader.setKeyName(KEY_NAME);
        context.put(KEY_NAME, expected);
    }

    @Test
    public void testOneRead() throws Exception {
        reader.open(context);
        assertEquals(expected, reader.read());
    }

    @Test
    public void testTwoRead() throws Exception {
        reader.open(context);
        reader.read();
        assertEquals(null, reader.read());
    }

    @Test
    public void testEmptyContext() throws Exception {
        context.remove(KEY_NAME);
        reader.open(context);

        assertEquals(null, reader.read());
    }
}