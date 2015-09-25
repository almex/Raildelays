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
public abstract class AbstractExecutionContextJobParametersExtractorTest<T extends AbstractExecutionContextJobParametersExtractor> {

    public static final String KEY = "objectValue";
    protected Date dateValue = new Date();
    protected Float floatValue = 1.0f;
    protected Double doubleValue = 2.0;
    protected Integer integerValue = 3;
    protected Long longValue = 4L;
    protected String stringValue = "foo";
    protected Object objectValue = new Object();
    protected T jobParametersExtractor;
    protected StepExecution stepExecution;


    @Before
    public void setUp() throws Exception {
        stepExecution = MetaDataInstanceFactory.createStepExecution();
    }

    /**
     * We expect that all type of parameters coming from the step {@link ExecutionContext} are injected as a
     * {@code JobParameter}.
     */
    @Test
    public void testGetJobParameters() throws Exception {
        JobParameters jobParameters = jobParametersExtractor.getJobParameters(new FlowJob(), stepExecution);

        assertAll(jobParameters);
    }

    /**
     * We expect that if we filter on one key only one parameter with the right value
     */
    @Test
    public void testGetJobParametersFromKeys() throws Exception {
        jobParametersExtractor.setKeys(new String[]{KEY});
        jobParametersExtractor.setUseAllContextAttributes(false);

        JobParameters jobParameters = jobParametersExtractor.getJobParameters(new FlowJob(), stepExecution);

        assertOneKey(jobParameters);
    }

    protected void assertAll(JobParameters jobParameters) {
        Assert.assertEquals(dateValue, jobParameters.getDate("dateValue"));
        Assert.assertEquals(floatValue.doubleValue(), jobParameters.getDouble("floatValue").doubleValue(), 0.0);
        Assert.assertEquals(doubleValue, jobParameters.getDouble("doubleValue"));
        Assert.assertEquals(integerValue.longValue(), jobParameters.getLong("integerValue").longValue());
        Assert.assertEquals(longValue, jobParameters.getLong("longValue"));
        Assert.assertEquals(stringValue, jobParameters.getString("stringValue"));
        Assert.assertEquals(objectValue.toString(), jobParameters.getString(KEY));
        Assert.assertEquals(null, jobParameters.getString("nullValue"));
    }

    protected void assertOneKey(JobParameters jobParameters) {
        Assert.assertEquals(1, jobParameters.getParameters().size());
        Assert.assertEquals(objectValue.toString(), jobParameters.getString(KEY));
    }

    protected void initialize(ExecutionContext context) {
        context.put("dateValue", dateValue);
        context.put("floatValue", floatValue);
        context.put("doubleValue", doubleValue);
        context.put("integerValue", integerValue);
        context.put("longValue", longValue);
        context.put("stringValue", stringValue);
        context.put("objectValue", objectValue);
        context.put("nullValue", null);
    }
}
