package org.springframework.batch.core.step.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;


/**
 * @author Almex
 */
public class JobExecutionContextJobParametersExtractorTest {

    private JobExecutionContextJobParametersExtractor jobParametersExtractor;

    @Before
    public void setUp() throws Exception {
        jobParametersExtractor = new JobExecutionContextJobParametersExtractor();
    }

    /**
     * We expect that all type of parameters coming from the job {@link ExecutionContext} are injected as a
     * {@code JobParameter}.
     */
    @Test
    public void testGetJobParameters() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();
        ExecutionContext context = stepExecution.getJobExecution().getExecutionContext();
        Date dateValue = new Date();
        Float floatValue = 1.0f;
        Double doubleValue = 2.0;
        Integer integerValue = 3;
        Long longValue = 4L;
        String stringValue = "foo";
        Object objectValue = new Object();

        context.put("dateValue", dateValue);
        context.put("floatValue", floatValue);
        context.put("doubleValue", doubleValue);
        context.put("integerValue", integerValue);
        context.put("longValue", longValue);
        context.put("stringValue", stringValue);
        context.put("objectValue", objectValue);
        context.put("nullValue", null);

        JobParameters jobParameters = jobParametersExtractor.getJobParameters(new FlowJob(), stepExecution);

        Assert.assertEquals(dateValue, jobParameters.getDate("dateValue"));
        Assert.assertEquals(floatValue.doubleValue(), jobParameters.getDouble("floatValue").doubleValue(), 0.0);
        Assert.assertEquals(doubleValue, jobParameters.getDouble("doubleValue"));
        Assert.assertEquals(integerValue.longValue(), jobParameters.getLong("integerValue").longValue());
        Assert.assertEquals(longValue, jobParameters.getLong("longValue"));
        Assert.assertEquals(stringValue, jobParameters.getString("stringValue"));
        Assert.assertEquals(objectValue.toString(), jobParameters.getString("objectValue"));
        Assert.assertEquals(null, jobParameters.getString("nullValue"));
    }
}