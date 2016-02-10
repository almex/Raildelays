package be.raildelays.server.scheduler;

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @author Almex
 * @since 2.0
 */
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MainJob implements Job {

    private BatchStartAndRecoveryService service;
    private JobParameters jobParameters;

    private static final Logger LOGGER = LoggerFactory.getLogger(MainJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            List<LocalDate> dates = ExcelFileUtils.generateListOfDates();

            for (LocalDate date : dates) {
                JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

                builder.addDate("date", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                JobExecution jobExecution = service.startNewInstance("mainJob", jobParameters);

                if (jobExecution.getStatus().isUnsuccessful()) {
                    throw new JobExecutionException("Job 'mainJob' has FAILED!");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error when starting the job: ", e);
        }
    }

    public void setService(BatchStartAndRecoveryService service) {
        this.service = service;
    }

    public void setJobParameters(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }
}
