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

import java.util.Arrays;
import java.util.Date;

public class LineStopMapperProcessorTest {

    /*
     * The System Under Test
     */
    private LineStopMapperProcessor processor;

    private TwoDirections input;

    private LineStop expected;

    private static final Date TODAY = new Date();

    private static final String LANGUAGE = Language.EN.name();

    private static final String DUMMY = "dummy";

    private static final String DEPARTURE_STATION = "departure";

    private static final String INTERMEDIATE_STATION = "intermediate";

    private static final String ARRIVAL_STATION = "arrival";

    @Before
    public void setUp() throws Exception {
        final TrainDao trainDaoMock = EasyMock.createMock(TrainDao.class);
        final StationDao stationDaoMock = EasyMock.createMock(StationDao.class);
        final Train train = new Train(DUMMY);
        final Direction departure = new Direction(new be.raildelays.domain.railtime.Train(DUMMY));
        final Direction arrival = new Direction(new be.raildelays.domain.railtime.Train(DUMMY));

        processor = new LineStopMapperProcessor();
        processor.setStationDao(stationDaoMock);
        processor.setTrainDao(trainDaoMock);
        processor.setLanguage(LANGUAGE);
        processor.setDate(TODAY);

        EasyMock.expect(stationDaoMock.findByEnglishName(DEPARTURE_STATION)).andReturn(new Station(DEPARTURE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(INTERMEDIATE_STATION)).andReturn(new Station(INTERMEDIATE_STATION));
        EasyMock.expect(stationDaoMock.findByEnglishName(ARRIVAL_STATION)).andReturn(new Station(ARRIVAL_STATION));
        EasyMock.expect(trainDaoMock.findByEnglishName(DUMMY)).andReturn(new Train(DUMMY)).anyTimes();
        EasyMock.replay(trainDaoMock, stationDaoMock);

        departure.setFrom(new be.raildelays.domain.railtime.Station(DEPARTURE_STATION));
        departure.setTo(new be.raildelays.domain.railtime.Station(ARRIVAL_STATION));
        departure.getSteps().addAll(Arrays.asList(
                new Step(0, DEPARTURE_STATION, new Date(), 0L, false),
                new Step(1, INTERMEDIATE_STATION, new Date(), 0L, false),
                new Step(2, ARRIVAL_STATION, new Date(), 0L, false)
        ));

        arrival.setFrom(new be.raildelays.domain.railtime.Station(DEPARTURE_STATION));
        arrival.setTo(new be.raildelays.domain.railtime.Station(ARRIVAL_STATION));
        arrival.getSteps().addAll(Arrays.asList(
                new Step(0, DEPARTURE_STATION, new Date(), 0L, false),
                new Step(1, INTERMEDIATE_STATION, new Date(), 0L, false),
                new Step(2, ARRIVAL_STATION, new Date(), 0L, false)
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

    }

    @Test
    public void testProcess() throws Exception {
        Assert.assertEquals(expected, processor.process(input));
    }
}