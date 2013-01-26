package be.raildelays.repository.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.repository.RailtimeTrainDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/repository/raildelays-repository-integration-context.xml","classpath:spring/test/raildelays-tx-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback=true)
public class StationJpaDaoIT {
	
	@Resource
	private RailtimeTrainDao railtimeTrainDao;
	
	@Test
	public void createTest() {
		RailtimeTrain train = railtimeTrainDao.save(new RailtimeTrain("466", "466"));
		
		Assert.assertNotNull("The create method should return a result", train);
		Assert.assertNotNull("The persisted station should returned with an id", train.getId());
	}
	
	@Test
	public void searchTest() {
		String id = "466";
		RailtimeTrain expected = railtimeTrainDao.save(new RailtimeTrain(id, id));
		RailtimeTrain train = railtimeTrainDao.findByRailtimeId(id);
		
		Assert.assertNotNull("The create method should return a result", train);
		Assert.assertEquals("We should retrieve the one previously created", expected, train);
	}

	
}
