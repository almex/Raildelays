package be.raildelays.javafx;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.step.job.JobParametersExtractor;

import java.util.Date;

public class BatchScheduledService extends ScheduledService<Integer> {
    private IntegerProperty count = new SimpleIntegerProperty();
    private JobParametersExtractor propertiesExtractor;
    private BatchStartAndRecoveryService service;
    private JobExecution jobExecution;
    private String jobName;
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchScheduledService.class);

    public BatchScheduledService() {
        this.jobExecution = null;
    }

    public void start(String jobName, Date date) {
        if (!isStarted()) {
            JobParameters jobParameters = propertiesExtractor.getJobParameters(null, null);
            JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

            builder.addDate("date", date);

            try {
                jobExecution = service.start(jobName, builder.toJobParameters());
            } catch (Exception e) {
                LOGGER.error("Error when starting the job: ", e);
            }
        }

        start();
    }

    @Override
    public void start() {

        super.start();
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            protected Integer call() {
                final int counter = getCount();

                try {
                    jobExecution = service.refresh(jobExecution);
                } catch (Exception e) {
                    LOGGER.error("Error when retrieving last status of the job execution: ", e);
                }

                count.set(counter + 1);

                return counter;
            }
        };
    }

    @Override
    public void reset() {
        super.reset();
        count.set(0);
        jobExecution = null;
    }

    @Override
    public void restart() {
        try {
            if (isStarted()) {
                jobExecution = service.restart(jobExecution.getId());
            }
        } catch (Exception e) {
            LOGGER.error("Error when restarting the job execution!", e);
        }

        super.restart();
    }

    public boolean stop() {
        boolean result = false;

        try {
            if (isStarted()) {
                jobExecution = service.stop(jobExecution.getId());
                result = jobExecution.isStopping();
            }
        } catch (Exception e) {
            LOGGER.error("Error when stopping the job execution!", e);
        }

        return result;
    }

    public boolean abandon() {
        boolean result = false;

        try {
            if (isStarted()) {
                jobExecution = service.abandon(jobExecution.getId());
                result = jobExecution.getStatus().equals(BatchStatus.ABANDONED);
            }
        } catch (Exception e) {
            LOGGER.error("Error when stopping the job execution!", e);
        }

        return result;
    }

    public boolean isStarted() {
        return jobExecution != null;
    }

    public final Integer getCount() {
        return count.get();
    }

    public final JobExecution getJobExecution() {
        return jobExecution;
    }

    public final IntegerProperty countProperty() {
        return count;
    }

    public void setPropertiesExtractor(JobParametersExtractor propertiesExtractor) {
        this.propertiesExtractor = propertiesExtractor;
    }

    public void setService(BatchStartAndRecoveryService service) {
        this.service = service;
    }
}