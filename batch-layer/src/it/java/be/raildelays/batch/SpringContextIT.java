package be.raildelays.batch;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"/spring/batch/raildelays-batch-integration-context.xml", "/jobs/batch-jobs-context.xml"})
public class SpringContextIT extends  AbstractJUnit4SpringContextTests {

	@Test
	public void testLoading() {
		assertTrue("The test should fail before this line", true);
	}
	
}
