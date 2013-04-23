package be.raildelays.batch;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {
"/jobs/search-delays-job-context.xml" })
public class SearchDelaysJobIT extends AbstractContextIT {

	/**
	 * SUT.
	 */
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void testGrabLineStop() {		
		BatchStatus batchStatus;
		
		try {
			Map<String, JobParameter> parameters = new HashMap<>();
			Calendar today = Calendar.getInstance();			
			Date date = null;
			
			if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
					today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				date = DateUtils.addDays(today.getTime(), -2);
			} else {			
				date = today.getTime();
			}

			parameters.put("input.file.path", new JobParameter("train-list.properties"));	
			parameters.put("date", new JobParameter(date));	
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
