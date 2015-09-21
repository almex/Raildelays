package org.springframework.batch.core.step.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

/**
 * Extract keys from a Job {@link ExecutionContext} and map them into an equivalent {@link JobParameter}.
 *
 * @author Almex
 * @since 2.0
 */
public class JobExecutionContextJobParametersExtractor extends AbstractExecutionContextJobParametersExtractor {

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        return addJobParametersFromContext(new JobParameters(), stepExecution.getJobExecution().getExecutionContext());
    }
}
