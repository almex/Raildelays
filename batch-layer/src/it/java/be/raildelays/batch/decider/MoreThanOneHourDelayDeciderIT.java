package be.raildelays.batch.decider;

import be.raildelays.batch.AbstractContextIT;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

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
    public void  testFlow() throws Exception {
        BatchStatus batchStatus;

        batchStatus = jobLauncherTestUtils.launchJob().getStatus();

        Assert.assertFalse(batchStatus.isUnsuccessful());
    }
}