package be.raildelays.batch;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@DirtiesContext // Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {
		"classpath:spring/batch/components/raildelays-batch-db-test.xml",
		"classpath:spring/batch/components/raildelays-batch-context.xml",
		"classpath:spring/batch/components/raildelays-batch-launcher.xml",
		"classpath:spring/service/raildelays-service-integration-test-context.xml",
		"classpath:spring/parser/raildelays-parser-integration-context.xml",
		"classpath:spring/streamer/raildelays-streamer-integration-context.xml"})
public class SpringContextIT {

	@Test
	public void testLoading() {
		assertTrue("The test should fail before this line", true);
	}

}
