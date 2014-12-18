package be.raildelays.batch.decider;

import be.raildelays.batch.AbstractContextIT;
import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {
        "/test/flow-job-context.xml"})
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