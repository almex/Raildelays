package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.CalendarDate;
import be.raildelays.batch.gtfs.Trip;
import org.easymock.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ItemStreamReader;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNull;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FilterUnscheduledTripProcessorTest extends EasyMockSupport {

    @TestSubject
    public FilterUnscheduledTripProcessor processor = new FilterUnscheduledTripProcessor();

    @Mock(type = MockType.NICE)
    private ItemStreamReader<CalendarDate> reader;

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

    /**
     * We expect that with no tripId the Trip is filtered.
     */
    @Test
    public void testProcess() throws Exception {

        expect(reader.read()).andReturn(new CalendarDate());
        expect(reader.read()).andReturn(null);

        replayAll();

        assertNull(processor.process(new Trip()));
    }
}