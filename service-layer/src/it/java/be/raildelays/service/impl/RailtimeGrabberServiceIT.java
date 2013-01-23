package be.raildelays.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import be.raildelays.service.RaildelaysGrabberService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/service/raildelays-service-integration-context.xml" })
public class RailtimeGrabberServiceIT {
	
	/**
	 * SUT.
	 */
	@Resource
	RaildelaysGrabberService grabberService;
	
	@Test
	public void test467LineStop() {
		Assert.assertNotNull(grabberService.grabTrainLine("467", new Date()));
	}

}
