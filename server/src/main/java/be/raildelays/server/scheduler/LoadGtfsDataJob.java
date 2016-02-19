package be.raildelays.server.scheduler;

import be.raildelays.batch.ExcelFileUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
public class LoadGtfsDataJob extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGtfsDataJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            List<LocalDate> dates = ExcelFileUtils.generateListOfDates();

            for (LocalDate date : dates) {
                JobParameters jobParameters = jobParametersExtractor.getJobParameters(null, null);
                JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

                builder.addDate("date", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                JobExecution jobExecution = service.startNewInstance("loadGtfsIntoDatabaseJob", builder.toJobParameters());

                if (jobExecution.getStatus().isUnsuccessful()) {
                    throw new JobExecutionException("Job 'mainJob' has FAILED!");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error when starting the job: ", e);
        }
    }

}
