package be.raildelays.repository.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDao;

import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import com.excilys.ebi.spring.dbunit.test.RollbackTransactionalDataSetTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:spring/repository/raildelays-repository-integration-test-context.xml",
		"classpath:spring/test/raildelays-tx-context.xml" })
@TransactionConfiguration
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		RollbackTransactionalDataSetTestExecutionListener.class })
@DataSet(value = "classpath:FindNextExpectedArrivalTimeIT.xml", tearDownOperation  = DBOperation.DELETE_ALL)
public class FindNextExpectedArrivalTimeIT {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FindNextExpectedArrivalTimeIT.class);

	@Resource
	private LineStopDao lineStopDao;

	@Test
	public void testFindNextExpectedArrivalTime() throws ParseException {
		Station station = new Station("Liège-Guillemins");
		Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm")
				.parse("2000-01-01 16:01");

		LOGGER.info("size={}", lineStopDao.findAll().size());
		for (LineStop lineStop : lineStopDao.findAll()) {
			LOGGER.info(lineStop.toString());
		}

		List<LineStop> lineStops = lineStopDao.findNextExpectedArrivalTime(
				station, date);

		Assert.assertEquals(1, lineStops.size());
		Assert.assertEquals("515", lineStops.get(0).getTrain());
	}

}
