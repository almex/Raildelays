package be.raildelays.batch.service;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Set;


public interface BatchStartAndRecoveryService {

    /**
     * Stop all running jobs (STARTING, STARTED, STOPPING)
     */
    void stopAllRunningJobs() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException;

    /**
     * This method must be call before any start of a job to recover
     * inconsitency within batch job repository due to an unproper shutdown.
     *
     * @throws NoSuchJobException
     * @throws NoSuchJobExecutionException
     * @throws JobExecutionNotRunningException
     * @throws InterruptedException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobInstanceAlreadyCompleteException
     * @throws JobRestartException
     * @throws JobParametersInvalidException
     * @throws NoSuchJobInstanceException
     */
    void markInconsistentJobsAsFailed() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException, InterruptedException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException;

    void restartAllFailedJobs() throws NoSuchJobException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException, JobExecutionAlreadyRunningException;

    void restartAllStoppedJobs();

    JobExecution start(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    JobExecution restart(Long jobExecutionId)
            throws JobExecutionAlreadyRunningException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException;

    JobExecution startNewInstance(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException,
            JobExecutionNotRunningException;

    JobExecution abandon(Long jobExecutionId)
            throws NoSuchJobExecutionException,
            JobExecutionAlreadyRunningException;

    Set<String> getJobNames();

}
