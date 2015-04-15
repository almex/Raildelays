package be.raildelays.batch;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
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
        File templateFile = new ClassPathResource("template.xls").getFile();
        String rootPath = templateFile.getParentFile().getAbsolutePath();

        parameters.put("excel.output.path", new JobParameter("file:" + rootPath + "/../retard_sncb.xls"));
        parameters.put("excel.input.path", new JobParameter("file:" + rootPath + "/6monthsDelays/*.xls"));
        //parameters.put("excel.archive.path", new JobParameter("file:batch-layer/target/#{java.time.LocalDate.now().toString()}/retard_sncb.xls"));
        parameters.put("excel.archive.path", new JobParameter("file:" + rootPath + "/../" + LocalDate.now().toString() + "/retard_sncb.xls"));
        parameters.put("excel.input.template", new JobParameter(templateFile.getAbsolutePath()));
        parameters.put("language", new JobParameter("en"));
        parameters.put("threshold.date", new JobParameter(Date.from(LocalDate.of(2013, 11, 13).atStartOfDay().toInstant(ZoneOffset.UTC))));


        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(new JobParameters(parameters));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
    }

}
