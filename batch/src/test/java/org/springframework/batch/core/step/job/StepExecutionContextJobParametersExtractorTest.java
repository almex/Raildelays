package org.springframework.batch.core.step.job;

import org.junit.Before;

/**
 * @author Almex
 */
public class StepExecutionContextJobParametersExtractorTest
        extends AbstractExecutionContextJobParametersExtractorTest<StepExecutionContextJobParametersExtractor> {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        jobParametersExtractor = new StepExecutionContextJobParametersExtractor();
        initialize(stepExecution.getExecutionContext());
    }
}