package be.raildelays.batch;

import com.excilys.ebi.spring.dbunit.config.DBOperation;
import com.excilys.ebi.spring.dbunit.test.DataSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;
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
@ContextConfiguration(locations = {"/jobs/main-job-context.xml"})
@DataSet(value = "classpath:GenerateExcelFilesIT.xml", tearDownOperation = DBOperation.DELETE_ALL)
public class GenerateExcelFilesIT extends AbstractContextIT {

    @Test
    public void testStep2() throws ParseException, IOException {
        final Map<String, JobParameter> parameters = new HashMap<>();

//        parameters.put("input.file.path", new JobParameter("train-list.properties"));
        parameters.put("date", new JobParameter(new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000")));
        parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
        parameters.put("station.b.name", new JobParameter("Bruxelles-Central"));
        parameters.put("output.file.path", new JobParameter("file:./output.dat"));
        parameters.put("excel.output.path", new JobParameter("./output.xls"));
        parameters.put("excel.input.template", new JobParameter(new ClassPathResource("template.xls").getFile().getAbsolutePath()));
        parameters.put("lang", new JobParameter("en"));


        JobExecution jobExecution = getJobLauncherTestUtils().launchStep("generateExcelFiles", new JobParameters(parameters));

        Assert.assertFalse(jobExecution.getStatus().isUnsuccessful());
        Assert.assertEquals(1, jobExecution.getStepExecutions().toArray(new StepExecution[1])[0].getSkipCount());
    }

}
