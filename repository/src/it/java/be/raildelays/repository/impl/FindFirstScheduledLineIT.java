package be.raildelays.repository.impl;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDao;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;

@DataSet(value = "classpath:FindArrivalDelaysIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL, dataSourceSpringName = "dataSource")
public class FindFirstScheduledLineIT extends AbstractIT {

    @Resource
    private LineStopDao lineStopDao;

    @Test
    public void testFound() throws ParseException {
        LineStop result = lineStopDao.findFistScheduledLine(new TrainLine("466"), new Station("Bruxelles-Central"));

        Assert.assertEquals(LocalDate.of(2000, 1, 1), result.getDate());
    }

    @Test
    public void testNotFound() throws ParseException {
        LineStop result = lineStopDao.findFistScheduledLine(new TrainLine("555"), new Station("Bruxelles-Central"));

        Assert.assertNull(result);
    }



}
