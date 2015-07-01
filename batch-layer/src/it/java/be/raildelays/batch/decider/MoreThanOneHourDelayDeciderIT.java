package be.raildelays.batch.decider;

import be.raildelays.batch.AbstractContextIT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {"/test/flow-job-context.xml"})
public class MoreThanOneHourDelayDeciderIT extends AbstractContextIT {

    /**
     * SUT.
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testCompleted() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParameters(
                        Collections.singletonMap("thresholdDelay", new JobParameter(100L))));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testCompletedWith60mDelay() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParameters(
                        Collections.singletonMap("thresholdDelay", new JobParameter(60L))));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
    }


    @Test
    public void testFailed() throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParameters(
                        Collections.singletonMap("thresholdDelay", new JobParameter(-1L))));

        Assert.assertTrue(jobExecution.getStatus().isUnsuccessful());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
    }
}