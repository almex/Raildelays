package be.raildelays.batch.processor;

import be.raildelays.batch.gtfs.Stop;
import be.raildelays.batch.gtfs.StopTime;
import be.raildelays.batch.gtfs.Trip;
import be.raildelays.domain.Language;
import org.easymock.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.batch.item.ItemStreamReader;

import java.time.LocalDate;
import java.util.Arrays;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;

/**
 * @author Almex
 */
public class BuildLineStopProcessorTest extends EasyMockSupport {

    private final Trip item = new Trip();

    @TestSubject
    public BuildLineStopProcessor processor = new BuildLineStopProcessor();
    @Mock(type = MockType.NICE)
    private ItemStreamReader<Stop> stopsReader;

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

    @Before
    public void setUp() throws Exception {
        StopTime stopTime = new StopTime();

        stopTime.setStopId("1");
        processor.setDate(LocalDate.now());
        processor.setStopsReader(stopsReader);
        item.setStopTimes(Arrays.asList(stopTime, new StopTime()));
    }

    @Test
    public void testProcessEN() throws Exception {
        Stop stop = new Stop();

        stop.setStopId("1");
        stop.setStopName("foo / bar");
        stop.setLocationType(Stop.LocationType.NOT_PHYSICAL);
        processor.setLang(Language.EN);

        expect(stopsReader.read()).andReturn(stop);

        replayAll();

        assertNotNull(processor.process(item));
    }

    @Test
    public void testProcessFR() throws Exception {
        Stop stop = new Stop();

        stop.setStopId("1");
        stop.setStopName("foo / bar");
        stop.setLocationType(Stop.LocationType.NOT_PHYSICAL);
        processor.setLang(Language.FR);

        expect(stopsReader.read()).andReturn(stop);

        replayAll();

        assertNotNull(processor.process(item));
    }

    @Test
    public void testProcessNL() throws Exception {
        Stop stop = new Stop();

        stop.setStopId("1");
        stop.setStopName("foo / bar");
        stop.setLocationType(Stop.LocationType.NOT_PHYSICAL);
        processor.setLang(Language.NL);

        expect(stopsReader.read()).andReturn(stop);

        replayAll();

        assertNotNull(processor.process(item));
    }
}