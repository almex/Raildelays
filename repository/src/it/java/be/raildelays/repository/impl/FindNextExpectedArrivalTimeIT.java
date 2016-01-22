package be.raildelays.repository.impl;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDao;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;

@DataSet(value = "classpath:FindNextExpectedArrivalTimeIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL, dataSourceSpringName = "dataSource")
public class FindNextExpectedArrivalTimeIT extends AbstractIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindNextExpectedArrivalTimeIT.class);

    @Resource
    private LineStopDao lineStopDao;

    @Test
    public void testFindNextExpectedArrivalTime() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDateTime date = LocalDateTime.parse("2000-01-01T16:27:00");

        LOGGER.info("size={}", lineStopDao.findAll().size());
        for (LineStop lineStop : lineStopDao.findAll()) {
            LOGGER.info(lineStop.toString());
        }

        List<LineStop> lineStops = lineStopDao.findNextExpectedArrivalTime(station, date);

        Assert.assertEquals(3, lineStops.size());
        Assert.assertEquals("1715", lineStops.get(0).getTrainLine().getName());
    }

}
