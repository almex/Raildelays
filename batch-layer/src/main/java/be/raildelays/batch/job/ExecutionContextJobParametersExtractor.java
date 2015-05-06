package be.raildelays.batch.job;

import org.springframework.batch.core.*;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.item.ExecutionContext;

import java.util.Date;
import java.util.Map;

/**
 * The goal is on top of the {@link DefaultJobParametersExtractor} behavior to extract
 * all keys from an {@link ExecutionContext} as a {@link JobParameter} for the embedded job.
 */
public class ExecutionContextJobParametersExtractor extends DefaultJobParametersExtractor {

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        JobParameters jobParameters = super.getJobParameters(job, stepExecution);
        JobExecution jobExecution = stepExecution.getJobExecution();

        jobParameters = addJobParametersFromContext(jobParameters, stepExecution.getExecutionContext());
        jobParameters = addJobParametersFromContext(jobParameters, jobExecution.getExecutionContext());

        return jobParameters;
    }


    private static JobParameters addJobParametersFromContext(JobParameters jobParameters, ExecutionContext context) {
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

        for (Map.Entry<String, Object> entry : context.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Date) {
                builder.addDate(entry.getKey(), (Date) value);
            } else if (value instanceof Long) {
                builder.addLong(entry.getKey(), (Long) value);
            } else if (value instanceof Double) {
                builder.addDouble(entry.getKey(), (Double) value);
            } else if (value instanceof String) {
                builder.addString(entry.getKey(), (String) value);
            }
        }

        return builder.toJobParameters();
    }
}
