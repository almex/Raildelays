package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
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
			SimpleDateFormat formater = new SimpleDateFormat("dd/mm/yyyy");
			Map<String, JobParameter> parameters = new HashMap<>();

			parameters.put("input.file.path", new JobParameter("train-list.properties"));	
			parameters.put("date", new JobParameter(formater.format(new Date())));	
			parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));	
			parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));	
			parameters.put("output.file.path", new JobParameter("file:./output.dat"));	
			
			batchStatus = jobLauncherTestUtils.launchJob(new JobParameters(parameters)).getStatus();
			
			Assert.assertEquals(BatchStatus.COMPLETED, batchStatus);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Your batch job has failed due to an exception.");
		}

        
	}
	
}
