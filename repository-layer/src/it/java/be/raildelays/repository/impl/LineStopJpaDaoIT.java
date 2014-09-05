package be.raildelays.repository.impl;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.RailtimeTrainDao;
import be.raildelays.repository.TrainDao;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LineStopJpaDaoIT extends AbstractIT {

    @Resource
    private LineStopDao lineStopDao;

    @Resource
    private TrainDao trainDao;

    @Resource
    private RailtimeTrainDao railtimeTrainDao;

    @Test
    public void testSave() {
        assertNotNull(
                "Creation should return a result",
                lineStopDao.save(new LineStop.Builder().date(new Date())
                        .train(new Train("466"))
                        .station(new Station("Liège-Guillemins"))
                        .arrivalTime(new TimestampDelay())
                        .departureTime(new TimestampDelay()).canceled(false)
                        .build()));
    }

    @Test
    public void testFindByTrainAndDate() {
        Date date = new Date();
        Train train = trainDao.saveAndFlush(new Train("466"));
        LineStop expectedLineStop = lineStopDao.save(new LineStop.Builder().date(date)
                .train(train)
                .station(new Station("Liège-Guillemins"))
                .arrivalTime(new TimestampDelay())
                .departureTime(new TimestampDelay())
                .canceled(false)
                .build());
        List<LineStop> lineStops = lineStopDao.findByTrainAndDate(train, date);

        assertEquals("You should have a certain number of results.", 1, lineStops.size());
        assertEquals("You should have the same result as expected.", expectedLineStop, lineStops.get(0));
    }

    @Test
    public void testFindByTrainIdAndDate() {
        Date date = new Date();
        Train train = trainDao.saveAndFlush(new Train("466"));
        LineStop expectedLineStop = lineStopDao.save(new LineStop.Builder().date(date)
                .train(train)
                .station(new Station("Liège-Guillemins"))
                .arrivalTime(new TimestampDelay())
                .departureTime(new TimestampDelay())
                .canceled(false)
                .build());
        LineStop lineStop = lineStopDao.findByTrainIdAndDate(expectedLineStop.getTrain().getId(), date);

        assertEquals("You should have the same result as expected.", expectedLineStop, lineStop);
    }
}
