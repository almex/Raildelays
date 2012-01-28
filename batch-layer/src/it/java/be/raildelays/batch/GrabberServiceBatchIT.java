package be.raildelays.batch;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/spring/batch/raildelays-batch-integration-context.xml","classpath:/jobs/batch-jobs-context.xml"})
public class GrabberServiceBatchIT {

	/**
	 * SUT.
	 */
	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
	
		
	@Test
	public void testGrabLineStop() {		
		BatchStatus batchStatus;
		
		try {
			batchStatus = jobLauncherTestUtils.launchJob().getStatus();
			
			Assert.assertEquals(BatchStatus.COMPLETED, batchStatus);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Your batch job has failed due to an exception.");
		}

        
	}
	
}
