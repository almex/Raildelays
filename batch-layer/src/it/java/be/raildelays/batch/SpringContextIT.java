package be.raildelays.batch;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@DirtiesContext // Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {
"/jobs/retrieve-data-from-railtime-job-context.xml" })
public class SpringContextIT extends AbstractContextIT {

	@Test
	public void testLoading() {
		assertTrue("The test should fail before this line", true);
	}

}
