package org.springframework.batch.core.step.job;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowJob;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

/**
 * @author Almex
 */
public class CompositeJobParametersExtractorTest {

    private CompositeJobParametersExtractor jobParametersExtractor;

    @Before
    public void setUp() throws Exception {
        jobParametersExtractor = new CompositeJobParametersExtractor();
        jobParametersExtractor.setDelegates(new JobParametersExtractor[]{(
                job, stepExecution) -> new JobParametersBuilder().addDate("date", new Date()).toJobParameters()
        });
    }

    /**
     * We expect to get the JobParameter in return via the composition.
     */
    @Test
    public void testGetJobParameters() throws Exception {
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        Assert.assertNotNull(jobParametersExtractor.getJobParameters(new FlowJob(), stepExecution).getDate("date"));
    }
}