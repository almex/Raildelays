package be.raildelays.repository.impl;

import be.raildelays.delays.Delays;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
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
public class FindFirstScheduledLineIT extends AbstractIT {

    @Resource
    private LineStopDao lineStopDao;

    @Test
    public void testFindArrivalDelaysPageable() throws ParseException {
        LineStop result = lineStopDao.findFistScheduledLine(new Train("466"), new Station("Bruxelles-Central"));

        Assert.assertEquals(LocalDate.of(2000, 1, 1), result.getDate());
    }



}
