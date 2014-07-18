package be.raildelays.batch.listener;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

/**
 * Add jobExeuctionId to {@link org.apache.logging.log4j.ThreadContext} to be able to create
 * log direction based on that number.
 *
 * @author Almex
 */
public class LoggerContextJobListener implements JobExecutionListener {


    public static final String JOB_EXECUTION_ID = "jobExecutionId";
    public static final String JOB_INSTANCE_ID = "jobInstanceId";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        ThreadContext.put(JOB_EXECUTION_ID, jobExecution.getId().toString());
        ThreadContext.put(JOB_INSTANCE_ID, jobExecution.getJobId().toString());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        ThreadContext.remove(JOB_EXECUTION_ID);
        ThreadContext.remove(JOB_INSTANCE_ID);
    }
}
