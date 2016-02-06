package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.StopTime;
import be.raildelays.batch.gtfs.Trip;
import org.easymock.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ItemStreamReader;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class FindStopTimesProcessorTest extends EasyMockSupport {

    @TestSubject
    public FindStopTimesProcessor processor = new FindStopTimesProcessor();

    @Mock(type = MockType.NICE)
    private ItemStreamReader<StopTime> reader;

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

    /**
     * We expect that with no tripId the StopTime list is empty.
     */
    @Test
    public void testProcess() throws Exception {

        expect(reader.read()).andReturn(new StopTime());
        expect(reader.read()).andReturn(null);

        replayAll();

        Trip trip = processor.process(new Trip());

        assertNotNull(trip);
        assertTrue(trip.getStopTimes().isEmpty());
    }
}