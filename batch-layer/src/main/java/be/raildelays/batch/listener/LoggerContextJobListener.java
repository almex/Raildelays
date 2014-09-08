package be.raildelays.batch.listener;

import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Add jobExecutionId to {@link org.apache.logging.log4j.ThreadContext} to be able to create
 * log direction based on that number.
 *
 * @author Almex
 */
public class LoggerContextJobListener implements JobExecutionListener {


    public static final String JOB_EXECUTION_ID = "jobExecutionId";
    public static final String JOB_INSTANCE_ID = "jobInstanceId";
    public static final String DATE_PARAMETER = "dateParameter";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Date date = jobExecution.getJobParameters().getDate("date");
        MDC.put(JOB_EXECUTION_ID, jobExecution.getId().toString());
        MDC.put(JOB_INSTANCE_ID, jobExecution.getJobId().toString());
        MDC.put(DATE_PARAMETER, new SimpleDateFormat("yyyy-MM-dd").format(date));
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        MDC.remove(JOB_EXECUTION_ID);
        MDC.remove(JOB_INSTANCE_ID);
        MDC.remove(DATE_PARAMETER);
    }
}
