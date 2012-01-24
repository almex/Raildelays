package be.raildelays.service.impl;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import be.raildelays.service.RaildelaysGrabberService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/service/raildelays-service-integration-context.xml"})
public class RailtimeGrabberServiceIT {

	/**
	 * SUT.
	 */
	@Autowired
	RaildelaysGrabberService grabberService;
	
		
	@Test
	public void testGrabLineStop() {
		Assert.assertNotNull(grabberService.grabTrainLine("466", new Date()));
	}
	
}
