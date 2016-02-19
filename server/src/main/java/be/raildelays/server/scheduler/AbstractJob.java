package be.raildelays.server.scheduler;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.quartz.Job;
import org.springframework.batch.core.step.job.JobParametersExtractor;

/**
 * Parent of all our Quartz Jobs.
 *
 * @author Almex
 * @since 2.0
 */
public abstract class AbstractJob implements Job {

    protected BatchStartAndRecoveryService service;
    protected JobParametersExtractor jobParametersExtractor;

    public void setService(BatchStartAndRecoveryService service) {
        this.service = service;
    }

    public void setJobParametersExtractor(JobParametersExtractor jobParametersExtractor) {
        this.jobParametersExtractor = jobParametersExtractor;
    }
}
