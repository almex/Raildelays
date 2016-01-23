package be.raildelays.repository.impl;

import be.raildelays.delays.Delays;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDao;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DataSet(value = "classpath:FindArrivalDelaysIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL, dataSourceSpringName = "dataSource")
public class FindArrivalDelaysIT extends AbstractIT {

    @Resource
    private LineStopDao lineStopDao;

    @Test
    public void testFindArrivalDelaysPageable() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDate date = LocalDate.parse("2000-01-01");
        List<LineStop> lineStops = new ArrayList<>();

        lineStops.addAll(lineStopDao.findArrivalDelays(date, station, Delays.toMillis(15L), new PageRequest(0, 1, new Sort(
                Sort.Direction.ASC, "arrivalTime"
        ))).getContent());
        lineStops.addAll(lineStopDao.findArrivalDelays(date, station, Delays.toMillis(15L), new PageRequest(1, 1, new Sort(
                Sort.Direction.ASC, "arrivalTime"
        ))).getContent());
        lineStops.addAll(lineStopDao.findArrivalDelays(date, station, Delays.toMillis(15L), new PageRequest(2, 1, new Sort(
                Sort.Direction.ASC, "arrivalTime"
        ))).getContent());


        Assert.assertEquals(
                Arrays.asList(466L, 477L, 515L),
                extractTrainIds(lineStops)
        );
    }

    @Test
    public void testFindArrivalDelays() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDate date = LocalDate.parse("2000-01-01");
        List<LineStop> lineStops = lineStopDao.findArrivalDelays(date, station, Delays.toMillis(15L));

        Assert.assertEquals(
                Arrays.asList(466L, 477L, 515L),
                extractTrainIds(lineStops)
        );
    }

    @Test
    public void testFindDepartureDelaysPageable() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDate date = LocalDate.parse("2000-01-01");
        List<LineStop> lineStops = new ArrayList<>();

        lineStops.addAll(lineStopDao.findDepartureDelays(date, station, Delays.toMillis(15L), new PageRequest(0, 1, new Sort(
                Sort.Direction.ASC, "departureTime"
        ))).getContent());
        lineStops.addAll(lineStopDao.findDepartureDelays(date, station, Delays.toMillis(15L), new PageRequest(1, 1, new Sort(
                Sort.Direction.ASC, "departureTime"
        ))).getContent());
        lineStops.addAll(lineStopDao.findDepartureDelays(date, station, Delays.toMillis(15L), new PageRequest(2, 1, new Sort(
                Sort.Direction.ASC, "departureTime"
        ))).getContent());

        Assert.assertEquals(
                Arrays.asList(466L, 477L, 1715L),
                extractTrainIds(lineStops)
        );
    }

    @Test
    public void testFindDepartureDelays() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDate date = LocalDate.parse("2000-01-01");
        List<LineStop> lineStops = lineStopDao.findDepartureDelays(date, station, Delays.toMillis(15L));

        Assert.assertEquals(
                Arrays.asList(466L, 477L, 1715L),
                extractTrainIds(lineStops)
        );
    }

    private static List<Long> extractTrainIds(List<LineStop> lineStops) {
        return lineStops.stream()
                .map(LineStop::getTrainLine)
                .map(TrainLine::getRouteId)
                .sorted()
                .collect(Collectors.toList());
    }

}
