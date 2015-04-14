package be.raildelays.batch;

import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@DirtiesContext
// Because of issue [SPR-8849] (https://jira.springsource.org/browse/SPR-8849)
@ContextConfiguration(locations = {"/jobs/handle-max-months-job-context.xml"})
public class HandleDelayMoreThanOneHourIT extends AbstractContextIT {

    /**
     * SUT.
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testCompleted() throws Exception {
        final Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("excel.output.path", new JobParameter("./output.xls"));
        parameters.put("excel.archive.path", new JobParameter("./#{java.time.LocalDate.now().toString()}/retard_sncb.xls"));
        parameters.put("excel.input.template", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));
        parameters.put("language", new JobParameter("en"));

        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(new JobParameters(parameters));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
    }

}
