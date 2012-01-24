package be.raildelays.repository.impl;

import java.util.Date;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/repository/raildelays-repository-integration-context.xml","classpath:spring/test/raildelays-tx-context.xml"})
@TransactionConfiguration(defaultRollback=true)
public class LineStopJpaDaoIT {
	
	@Resource
	private LineStopJpaDao lineStopDao;
	
	@Test
	@Transactional
	public void createTest() {
		Assert.assertNotNull("Creation should return a result", lineStopDao.createLineStop(new LineStop(new Train("466"), new Station("Liège-Guillemins"), new TimestampDelay(), new TimestampDelay())));
	}
	
	@Test
	@Transactional
	public void retrieveTest() {
		//Assert.assertNull("No data should get back.", lineStopDao.retrieveLineStop("466", new java.sql.Date(new Date().getTime())));
		LineStop lineStop = lineStopDao.createLineStop(new LineStop(new Train("466"), new Station("Liège-Guillemins"), new TimestampDelay(), new TimestampDelay()));
		Assert.assertEquals("You should have a certain number of results.", 0, lineStopDao.retrieveLineStop("466", new java.sql.Date(new Date().getTime())).size());
	}
	

	
}
