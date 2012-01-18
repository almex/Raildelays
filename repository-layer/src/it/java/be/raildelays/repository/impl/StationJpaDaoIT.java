package be.raildelays.repository.impl;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.StationDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/raildelays-dao-context.xml")
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class StationJpaDaoIT {
	
	@Resource
	private StationDao stationDao;
	
	@Test
	public void createTest() {
		Station station = stationDao.createStation(new Station("Liège-Guillemins"));
		Assert.assertNotNull("The create method should return a result", station);
		Assert.assertNotNull("The persisted station should returned with an id", station.getId());
	}
	
	@Test
	public void searchTest() {
		String name = "Liège-Guillemins";
		stationDao.createStation(new Station(name));
		Station station = stationDao.retrieveStation(name, Language.ENGLISH);
		Assert.assertNotNull("The create method should return a result", station);
	}

	
}
