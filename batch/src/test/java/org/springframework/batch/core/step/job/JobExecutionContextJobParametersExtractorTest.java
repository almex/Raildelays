package org.springframework.batch.core.step.job;

import org.junit.Before;


/**
 * @author Almex
 */
public class JobExecutionContextJobParametersExtractorTest extends AbstractExecutionContextJobParametersExtractorTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        jobParametersExtractor = new JobExecutionContextJobParametersExtractor();
        initialize(stepExecution.getJobExecution().getExecutionContext());
    }

}