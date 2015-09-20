package org.springframework.batch.core.step.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.test.MetaDataInstanceFactory;

/**
 * @author Almex
 */
public class JobParametersIncrementerJobParametersExtractorTest {

    public static final String KEY = "run.id";
    private JobParametersIncrementerJobParametersExtractor jobParametersExtractor;

    @Before
    public void setUp() throws Exception {
        jobParametersExtractor = new JobParametersIncrementerJobParametersExtractor();
        jobParametersExtractor.setJobParametersIncrementer(
                parameters -> new JobParametersBuilder().addLong(KEY, 0L).toJobParameters()
        );
    }

    /**
     * We expect to get a 'run.id' as a {@link JobParameters}.
     */
    @Test
    public void testGetJobParameters() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        JobParameters jobParameters = jobParametersExtractor.getJobParameters(new FlowJob(), stepExecution);

        Assert.assertNotNull(jobParameters.getLong(KEY));
    }
}