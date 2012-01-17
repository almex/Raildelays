package be.raildelays.service.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/raildelays-dao-context.xml", "classpath:spring/raildelays-service-context.xml"})
@Transactional
public class RailtimeGrabberServiceTest {

	/**
	 * SUT.
	 */
	@Autowired
	RailtimeGrabberService grabberService;
		
	@Test
	public void testGrabLineStop() {
		Assert.assertNotNull(grabberService.grabTrainLine("466", new Date()));
	}
	
}
