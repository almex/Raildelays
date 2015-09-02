package be.raildelays.repository.impl;

import be.raildelays.delays.Delays;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDao;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DataSet(value = "classpath:FindArrivalDelaysIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL, dataSourceSpringName = "dataSource")
public class FindArrivalDelaysIT extends AbstractIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindArrivalDelaysIT.class);

    @Resource
    private LineStopDao lineStopDao;

    @Test
    public void testFindArrivalDelays() throws ParseException {
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

        Assert.assertEquals(3, lineStops.size());
        Assert.assertEquals("515", lineStops.get(0).getTrain().getEnglishName());
        Assert.assertEquals("466", lineStops.get(1).getTrain().getEnglishName());
        Assert.assertEquals("477", lineStops.get(2).getTrain().getEnglishName());
    }

}
