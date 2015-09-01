package be.raildelays.repository.impl;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDaoCustom;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDate;

@DataSet(value = "classpath:FindArrivalDelaysIT.xml",
        tearDownOperation = DBOperation.DELETE_ALL, dataSourceSpringName = "dataSource")
public class FindArrivalDelaysIT extends AbstractIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindArrivalDelaysIT.class);

    @Resource
    private LineStopDaoCustom lineStopDao;

    @Test
    public void testFindArrivalDelays() throws ParseException {
        Station station = new Station("Bruxelles-Central");
        LocalDate date = LocalDate.parse("2000-01-01");

        Page<LineStop> lineStops = lineStopDao.findArrivalDelays(date, station, 15, new PageRequest(0, 3));

        Assert.assertEquals(3, lineStops.getContent().size());
        Assert.assertEquals("466", lineStops.getContent().get(0).getTrain().getEnglishName());
        Assert.assertEquals("477", lineStops.getContent().get(1).getTrain().getEnglishName());
        Assert.assertEquals("515", lineStops.getContent().get(2).getTrain().getEnglishName());
    }

}
