package be.raildelays.batch.job;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

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
        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

        jobParametersExtractor.getJobParameters(null, stepExecution);
    }
}