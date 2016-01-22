package be.raildelays.repository.impl;

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.TrainDao;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LineStopJpaDaoIT extends AbstractIT {

    @Resource
    private LineStopDao lineStopDao;

    @Resource
    private TrainDao trainDao;

    @Test
    public void testSave() {
        assertNotNull(
                "Creation should return a result",
                lineStopDao.save(new LineStop.Builder().date(LocalDate.now())
                        .train(new TrainLine.Builder(466L).build())
                        .station(new Station("Liège-Guillemins"))
                        .arrivalTime(TimeDelay.now())
                        .departureTime(TimeDelay.now())
                        .canceledDeparture(false)
                        .canceledArrival(false)
                        .build()));
    }

    @Test
    public void testFindByTrainAndDate() {
        LocalDate date = LocalDate.now();
        TrainLine trainLine = trainDao.saveAndFlush(new TrainLine.Builder(466L).build());
        LineStop expectedLineStop = lineStopDao.save(new LineStop.Builder().date(date)
                .train(trainLine)
                .station(new Station("Liège-Guillemins"))
                .arrivalTime(TimeDelay.now())
                .departureTime(TimeDelay.now())
                .canceledArrival(false)
                .canceledDeparture(false)
                .build());
        List<LineStop> lineStops = lineStopDao.findByTrainLineAndDate(trainLine, date);

        assertEquals("You should have a certain number of results.", 1, lineStops.size());
        assertEquals("You should have the same result as expectedTime.", expectedLineStop, lineStops.get(0));
    }

    @Test
    public void testFindByTrainIdAndDate() {
        LocalDate date = LocalDate.now();
        TrainLine trainLine = trainDao.saveAndFlush(new TrainLine.Builder(466L).build());
        LineStop expectedLineStop = lineStopDao.save(new LineStop.Builder().date(date)
                .train(trainLine)
                .station(new Station("Liège-Guillemins"))
                .arrivalTime(TimeDelay.now())
                .departureTime(TimeDelay.now())
                .canceledArrival(false)
                .canceledDeparture(false)
                .build());
        LineStop lineStop = lineStopDao.findByTrainLineIdAndDate(expectedLineStop.getTrainLine().getId(), date);

        assertEquals("You should have the same result as expectedTime.", expectedLineStop, lineStop);
    }
}
