package be.raildelays.service;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * We are not testing raildelays-service-integration-context.xml as it would
 * lead to create a databse somewhere than in <code>/target</code> making that
 * test less portable.
 * 
 * @author Almex
 */
@ContextConfiguration(locations = {
		"classpath:spring/repository/components/raildelays-dao-context.xml",
		"classpath:spring/service/components/raildelays-service-context.xml",
		"classpath:spring/service/components/raildelays-tx-context.xml",
		"classpath:spring/service/components/raildelays-validation-context.xml" })
public class SpringContextIT extends AbstractJUnit4SpringContextTests {

	@Test
	@Ignore
	//TODO try to solve issue related to No persistence exception translators 
	//found in bean factory. Cannot perform exception translation.
	public void testLoading() {
		assertTrue("The test should fail before this line", true);
	}

}
