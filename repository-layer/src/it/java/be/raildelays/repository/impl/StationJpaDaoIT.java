package be.raildelays.repository.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.Station;
import be.raildelays.repository.StationDao;

public class StationJpaDaoIT extends AbstractIT {

	@Resource
	private StationDao stationDao;

	@Test
	public void createTest() {
		Station station = stationDao.save(new Station("Liège-Guillemins"));

		Assert.assertNotNull("The create method should return a result",
				station);
		Assert.assertNotNull(
				"The persisted station should returned with an id",
				station.getId());
	}

	@Test
	public void searchTest() {
		String id = "Liège-Guillemins";
		Station expected = stationDao.save(new Station(id));
		Station station = stationDao.findByEnglishName(id);

		Assert.assertNotNull("The create method should return a result",
				station);
		Assert.assertEquals("We should retrieve the one previously created",
				expected, station);
	}

}
