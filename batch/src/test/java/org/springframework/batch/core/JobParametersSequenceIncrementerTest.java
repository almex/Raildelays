package org.springframework.batch.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;

/**
 * @author Almex
 */
public class JobParametersSequenceIncrementerTest {

    private JobParametersSequenceIncrementer jobParametersIncrementer;

    @Before
    public void setUp() throws Exception {
        jobParametersIncrementer = new JobParametersSequenceIncrementer();
        jobParametersIncrementer.setSequence(new AbstractDataFieldMaxValueIncrementer() {
            @Override
            protected long getNextKey() {
                return 0;
            }
        });
    }

    @Test
    public void testGetNext() throws Exception {
        JobParameters jobParameters = jobParametersIncrementer.getNext(new JobParameters());

        Assert.assertNotNull(jobParameters.getLong(JobParametersSequenceIncrementer.INCREMENTER_PARAMETER_NAME));
    }
}