package be.raildelays.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.unitils.dbunit.annotation.DataSet;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:spring/repository/raildelays-repository-integration-test-context.xml",
		"classpath:spring/test/raildelays-tx-context.xml" })
@Transactional
@TransactionConfiguration(defaultRollback = true)
@DataSet
public class FindNextExpectedArrivalTimeIT {

	@Resource
	private LineStopDao lineStopDao;

	@Test
	public void testFindNextExpectedArrivalTime() throws ParseException {
		Station station = new Station("Liège-Guillemins");
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");
		
		List<LineStop> lineStops = lineStopDao.findNextExpectedArrivalTime(station, date);
		
		Assert.assertEquals(1, lineStops.size());
		Assert.assertEquals("515", lineStops.get(0).getTrain());
	}

}
