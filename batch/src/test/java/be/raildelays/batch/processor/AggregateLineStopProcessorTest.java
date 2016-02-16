package be.raildelays.batch.processor;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainLineDao;
import org.easymock.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertNotNull;

/**
 * @author Almex
 */
public class AggregateLineStopProcessorTest extends EasyMockSupport {

    @TestSubject
    public AggregateLineStopProcessor processor = new AggregateLineStopProcessor();

    @Mock(type = MockType.NICE)
    private LineStopDao lineStopDao;
    @Mock(type = MockType.NICE)
    private TrainLineDao trainLineDao;
    @Mock(type = MockType.NICE)
    private StationDao stationDao;

    @Rule
    public EasyMockRule easyMockRule = new EasyMockRule(this);

    @Before
    public void setUp() throws Exception {
        processor.setLineStopDao(lineStopDao);
        processor.setStationDao(stationDao);
        processor.setTrainLineDao(trainLineDao);
        processor.afterPropertiesSet();
    }

    @Test
    public void testProcessAggregate() throws Exception {
        Station station = new Station("Liège-Guillemins");
        TrainLine trainLine = new TrainLine.Builder(1L).build(false);
        LineStop expected = new LineStop.Builder()
                .trainLine(trainLine)
                .station(station)
                .date(LocalDate.now())
                .addNext(new LineStop.Builder()
                        .trainLine(trainLine)
                        .station(new Station("Bruxelles-central"))
                        .date(LocalDate.now())
                )
                .build(false);

        expect(lineStopDao.findByRouteIdAndDateAndStationName(anyLong(), anyObject(), anyString())).andReturn(null);
        expect(stationDao.findByEnglishName(anyString())).andReturn(station);
        expect(trainLineDao.findByRouteId(anyLong())).andReturn(trainLine);

        replayAll();

        assertNotNull(processor.process(expected));
    }

    @Test
    public void testProcessMerge() throws Exception {
        Station station = new Station("Liège-Guillemins");
        TrainLine trainLine = new TrainLine.Builder(1L).build(false);
        LineStop expected = new LineStop.Builder()
                .trainLine(trainLine)
                .station(station)
                .date(LocalDate.now())
                .addNext(new LineStop.Builder()
                        .trainLine(trainLine)
                        .station(new Station("Bruxelles-central"))
                        .date(LocalDate.now())
                )
                .build(false);

        expect(lineStopDao.findByRouteIdAndDateAndStationName(anyLong(), anyObject(), anyString()))
                .andReturn(expected);

        replayAll();

        assertNotNull(processor.process(expected));
    }
}
