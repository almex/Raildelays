package org.springframework.batch.core.step.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.StepExecution;

/**
 * You can extract a parameter via a {@link JobParametersIncrementer}.
 * It's useful in case of nested Job injected into a partition to generate
 * a unique {@code JobInstance}.
 * <p>
 *
 *     <i>Example</i>:
 * <pre>
 * {@code
 *
 * <job id="mainJob">
 *      <step id="step1">
 *           <partition partitioner="partitioner">
 *                <handler task-executor="taskExecutor"/>
 *                <step>
 *                     <job job-parameters-extractor="jobParametersIncrementerJobParameterExtractor"
 *                          ref="nestedJob"/>
 *                </step>
 *           </partition>
 *      </step>
 * </job>
 * }
 * </pre>
 *
 * @author Almex
 */
public class JobParametersIncrementerJobParametersExtractor implements JobParametersExtractor {

    @Override
    public JobParameters getJobParameters(Job job, StepExecution stepExecution) {
        JobParameters result = new JobParameters();

        if (job != null && job.getJobParametersIncrementer() != null) {
            result = job.getJobParametersIncrementer().getNext(new JobParameters());
        }

        return result;
    }
}
