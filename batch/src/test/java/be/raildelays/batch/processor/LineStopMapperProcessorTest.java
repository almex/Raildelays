package be.raildelays.batch.processor;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@RunWith(BlockJUnit4ClassRunner.class)
public class LineStopMapperProcessorTest {

    private static final LocalDate TODAY = LocalDate.now();
    private static final String LANGUAGE = Language.EN.name();
    private static final String DUMMY = "dummy";
    private static final String DEPARTURE_STATION = "departure";
    private static final String INTERMEDIATE_STATION = "intermediate";
    private static final String ARRIVAL_STATION = "arrival";
    /*
     * The System Under Test
     */
    private LineStopMapperProcessor processor;
    private TwoDirections input;
    private LineStop expected;
    private TrainDao trainDaoMock;
    private StationDao stationDaoMock;

    @Before
    public void setUp() throws Exception {
        trainDaoMock = EasyMock.createMock(TrainDao.class);
        stationDaoMock = EasyMock.createMock(StationDao.class);
        final Train train = new Train(DUMMY);
        final Direction departure = new Direction(new be.raildelays.domain.railtime.Train(DUMMY));
        final Direction arrival = new Direction(new be.raildelays.domain.railtime.Train(DUMMY));

        processor = new LineStopMapperProcessor();
        processor.setStationDao(stationDaoMock);
        processor.setTrainDao(trainDaoMock);
        processor.setLanguage(LANGUAGE);
        processor.setDate(TODAY);

        departure.setFrom(new be.raildelays.domain.railtime.Station(DEPARTURE_STATION));
        departure.setTo(new be.raildelays.domain.railtime.Station(ARRIVAL_STATION));
        departure.getSteps().addAll(Arrays.asList(
                new Step(0, DEPARTURE_STATION, LocalDateTime.now(), 0L, false),
                new Step(1, INTERMEDIATE_STATION, LocalDateTime.now(), 0L, false),
                new Step(2, ARRIVAL_STATION, LocalDateTime.now(), 0L, false)
        ));

        arrival.setFrom(new be.raildelays.domain.railtime.Station(DEPARTURE_STATION));
        arrival.setTo(new be.raildelays.domain.railtime.Station(ARRIVAL_STATION));
        arrival.getSteps().addAll(Arrays.asList(
                new Step(0, DEPARTURE_STATION, LocalDateTime.now(), 0L, false),
                new Step(1, INTERMEDIATE_STATION, LocalDateTime.now(), 0L, false),
                new Step(2, ARRIVAL_STATION, LocalDateTime.now(), 0L, false)
        ));

        input = new TwoDirections(departure, arrival);
        expected = new LineStop.Builder()
                .train(train)
                .station(new Station(DEPARTURE_STATION))
                .date(TODAY)
                .addNext(new LineStop.Builder()
                        .train(train)
                        .station(new Station(INTERMEDIATE_STATION))
                        .date(TODAY)
                        .build())
                .addNext(new LineStop.Builder()
                        .train(train)
                        .station(new Station(ARRIVAL_STATION))
                        .date(TODAY)
                        .build())
                .build();
        processor.afterPropertiesSet();

    }

    @Test
    public void testProcessEN() throws Exception {
        EasyMock.expect(stationDaoMock.findByEnglishName(DEPARTURE_STATION)).andReturn(new Station(DEPARTURE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(INTERMEDIATE_STATION)).andReturn(new Station(INTERMEDIATE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(ARRIVAL_STATION)).andReturn(new Station(ARRIVAL_STATION));
        EasyMock.expect(trainDaoMock.findByEnglishName(DUMMY)).andReturn(new Train(DUMMY)).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        processor.setLanguage(Language.EN.name());

        Assert.assertEquals(expected, processor.process(input));
    }

    @Test
    public void testProcessNL() throws Exception {
        EasyMock.expect(stationDaoMock.findByDutchName(DEPARTURE_STATION)).andReturn(new Station(DEPARTURE_STATION));
        EasyMock.expect(stationDaoMock.findByDutchName(INTERMEDIATE_STATION)).andReturn(new Station(INTERMEDIATE_STATION));
        EasyMock.expect(stationDaoMock.findByDutchName(ARRIVAL_STATION)).andReturn(new Station(ARRIVAL_STATION));
        EasyMock.expect(trainDaoMock.findByDutchName(DUMMY)).andReturn(new Train(DUMMY)).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        processor.setLanguage(Language.NL.name());

        Assert.assertEquals(expected, processor.process(input));
    }

    @Test
    public void testProcessFR() throws Exception {
        EasyMock.expect(stationDaoMock.findByFrenchName(DEPARTURE_STATION)).andReturn(new Station(DEPARTURE_STATION));
        EasyMock.expect(stationDaoMock.findByFrenchName(INTERMEDIATE_STATION)).andReturn(new Station(INTERMEDIATE_STATION));
        EasyMock.expect(stationDaoMock.findByFrenchName(ARRIVAL_STATION)).andReturn(new Station(ARRIVAL_STATION));
        EasyMock.expect(trainDaoMock.findByFrenchName(DUMMY)).andReturn(new Train(DUMMY)).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        processor.setLanguage(Language.FR.name());

        Assert.assertEquals(expected, processor.process(input));
    }

    @Test
    public void testTrainNotFound() throws Exception {
        EasyMock.expect(stationDaoMock.findByEnglishName(DEPARTURE_STATION)).andReturn(new Station(DEPARTURE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(INTERMEDIATE_STATION)).andReturn(new Station(INTERMEDIATE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(ARRIVAL_STATION)).andReturn(new Station(ARRIVAL_STATION));
        EasyMock.expect(trainDaoMock.findByEnglishName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(trainDaoMock.findByDutchName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(trainDaoMock.findByFrenchName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        Assert.assertEquals(expected, processor.process(input));
    }

    @Test
    public void testStationNotFound() throws Exception {
        EasyMock.expect(stationDaoMock.findByEnglishName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(stationDaoMock.findByDutchName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(stationDaoMock.findByFrenchName(EasyMock.anyString())).andReturn(null).anyTimes();
        EasyMock.expect(trainDaoMock.findByEnglishName(DUMMY)).andReturn(new Train(DUMMY)).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        Assert.assertEquals(expected, processor.process(input));
    }
}