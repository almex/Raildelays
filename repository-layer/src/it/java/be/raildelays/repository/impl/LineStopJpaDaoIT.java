package be.raildelays.repository.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.RailtimeTrainDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/repository/raildelays-repository-integration-context.xml","classpath:spring/test/raildelays-tx-context.xml"})
@TransactionConfiguration(defaultRollback=true)
public class LineStopJpaDaoIT {
	
	@Resource
	private LineStopDao lineStopDao;
	
	@Resource
	private RailtimeTrainDao railtimeTrainDao;
	
	@Test
	@Transactional
	public void createTest() {
		assertNotNull("Creation should return a result", lineStopDao.save(new LineStop(new Date(), new Train("466"), new Station("Liège-Guillemins"), new TimestampDelay(), new TimestampDelay())));
	}
	
	@Test
	@Transactional
	public void retrieveTest() {
		//Assert.assertNull("No data should get back.", lineStopDao.retrieveLineStop("466", new java.sql.Date(new Date().getTime())));
		Train train = railtimeTrainDao.findByEnglishName("466");
		LineStop lineStop = lineStopDao.save(new LineStop(new Date(), new Train("466"), new Station("Liège-Guillemins"), new TimestampDelay(), new TimestampDelay()));
		assertEquals("You should have a certain number of results.", 0, lineStopDao.findByTrainAndDate(train, new java.sql.Date(new Date().getTime())).size());
	}
	

	
}
