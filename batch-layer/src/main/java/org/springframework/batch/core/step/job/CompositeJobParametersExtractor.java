package org.springframework.batch.core.step.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple composition of multiple {@link JobParametersExtractor}.
 *
 * @author Almex
 */
public class CompositeJobParametersExtractor implements JobParametersExtractor {

    private JobParametersExtractor[] delegates;

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        Map<String, JobParameter> jobParametersMap = new HashMap<>();

        for (JobParametersExtractor delegate : delegates) {
            jobParametersMap.putAll(delegate.getJobParameters(job, stepExecution).getParameters());
        }

        return new JobParameters(jobParametersMap);
    }

    public void setDelegates(JobParametersExtractor[] delegates) {
        this.delegates = delegates;
    }
}
