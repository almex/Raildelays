package org.springbatch.testcase;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import be.raildelays.batch.AbstractContextIT;

@ContextConfiguration(locations = { "/jobs/synchronize-job-context.xml" })
public class SynchronizeJobIT extends AbstractContextIT {

	/**
	 * SUT.
	 */
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void testSychronize() throws Exception {
		BatchStatus batchStatus;

		batchStatus = jobLauncherTestUtils.launchJob(
				jobLauncherTestUtils.getUniqueJobParameters()).getStatus();

		Assert.assertFalse(batchStatus.isUnsuccessful());
	}
}
