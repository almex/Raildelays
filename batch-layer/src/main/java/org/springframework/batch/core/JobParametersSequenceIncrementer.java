package org.springframework.batch.core;

import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import javax.annotation.Resource;

/**
 * In case of new instance for a job, this class provide the job parameters
 * incrementer by using a database sequence.
 *
 * @see org.springframework.batch.core.JobParametersIncrementer
 * @author Almex
 */
public class JobParametersSequenceIncrementer implements JobParametersIncrementer {

    /**
     * For legacy support we use the same name as the default one provided by
     * {@link org.springframework.batch.core.launch.support.RunIdIncrementer}
     */
    private static final String INCREMENTER_PARAMETER_NAME = "run.id";

    @Resource
    private DataFieldMaxValueIncrementer sequence;

    @Override
    public JobParameters getNext(final JobParameters jobParameters) {
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

        builder.addLong(INCREMENTER_PARAMETER_NAME, sequence.nextLongValue());

        return builder.toJobParameters();
    }

    public void setSequence(final DataFieldMaxValueIncrementer sequence) {
        this.sequence = sequence;
    }

}

