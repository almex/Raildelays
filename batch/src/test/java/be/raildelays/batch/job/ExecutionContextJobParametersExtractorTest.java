package be.raildelays.batch.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

/**
 * Created by xbmc on 06-05-15.
 */
public class ExecutionContextJobParametersExtractorTest {

    private ExecutionContextJobParametersExtractor jobParametersExtractor;

    @Before
    public void setUp() throws Exception {
        jobParametersExtractor = new ExecutionContextJobParametersExtractor();
    }

    @Test
    public void testGetJobParameters() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();

        builder.addString("foo", "bar");

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(builder.toJobParameters());

        stepExecution.getExecutionContext().put("foo2", "bar2");
        stepExecution.getExecutionContext().put("foo3", 1L);
        stepExecution.getJobExecution().getExecutionContext().put("foo4", 2.2);
        stepExecution.getJobExecution().getExecutionContext().put("foo5", new Date());

        JobParameters jobParameters = jobParametersExtractor.getJobParameters(new SimpleJob("foo"), stepExecution);
        Assert.assertNotNull(jobParameters);
        Assert.assertEquals(5, jobParameters.getParameters().size());
    }
}