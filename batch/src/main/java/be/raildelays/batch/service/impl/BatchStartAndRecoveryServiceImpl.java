/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.batch.service.impl;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

@Service("BatchStartAndRecoveryService")
public class BatchStartAndRecoveryServiceImpl implements BatchStartAndRecoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchStartAndRecoveryServiceImpl.class);

    private static final ExitStatus RECOVERY_STATUS = new ExitStatus("FAILED_FOR_RECOVERY", "Setted as failed in order to allow to restart this job instance");

    private JobRegistry jobRegistry;
    private JobExplorer jobExplorer;
    private JobRepository jobRepository;
    private JobLauncher jobLauncher;

    @Override
    public void stopAllRunningJobs() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        for (String jobName : jobRegistry.getJobNames()) {
            for (Long jobExecutionId : getRunningExecutions(jobName)) {
                stop(jobExecutionId);
            }
        }
    }

    @Override
    public void markInconsistentJobsAsFailed() throws NoSuchJobException,
            NoSuchJobExecutionException, JobExecutionNotRunningException,
            InterruptedException, JobExecutionAlreadyRunningException,
            JobInstanceAlreadyCompleteException, JobRestartException,
            JobParametersInvalidException, NoSuchJobInstanceException {
        Collection<String> jobNames = jobRegistry.getJobNames();

        for (String jobName : jobNames) {
            LOGGER.info("Searching to recover jobName={}...", jobName);

            // -- Retrieve all jobs marked as STARTED or STOPPING
            Set<Long> jobExecutionIds = getRunningExecutions(jobName);

            // -- Set incoherent running jobs as FAILED
            for (Long jobExecutionId : jobExecutionIds) {
                LOGGER.info("Found a job already running jobExecutionId={}.", jobExecutionId);

                // -- Set Job Execution as FAILED
                JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
                jobExecution.setEndTime(new Date());
                jobExecution.setStatus(BatchStatus.FAILED);
                jobExecution.setExitStatus(RECOVERY_STATUS);

                // -- Set all running Step Execution as FAILED
                jobExecution.getStepExecutions()
                        .stream()
                        .filter(stepExecution -> stepExecution.getStatus().isRunning())
                        .forEach(stepExecution -> {
                            stepExecution.setEndTime(new Date());
                            stepExecution.setStatus(BatchStatus.FAILED);
                            stepExecution.setExitStatus(RECOVERY_STATUS);

                            jobRepository.update(stepExecution);
                        });

                jobRepository.update(jobExecution);
                LOGGER.info("Setted job as FAILED!");
            }
        }
    }

    private Set<Long> getRunningExecutions(String jobName) throws NoSuchJobException {
        Set<Long> set = jobExplorer.findRunningJobExecutions(jobName)
                .stream()
                .map(JobExecution::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (set.isEmpty() && !jobRegistry.getJobNames().contains(jobName)) {
            throw new NoSuchJobException("No such job (either in registry or in historical data): " + jobName);
        }

        return set;
    }

    @Override
    public void restartAllFailedJobs() throws NoSuchJobException,
            JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
            JobRestartException, JobParametersInvalidException,
            NoSuchJobInstanceException, JobExecutionAlreadyRunningException {
        for (String jobName : jobRegistry.getJobNames()) {
            restartJobs(jobName, BatchStatus.FAILED);
        }
    }

    @Override
    public void restartAllStoppedJobs() throws NoSuchJobException,
            JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
            JobRestartException, JobParametersInvalidException,
            NoSuchJobInstanceException, JobExecutionAlreadyRunningException {
        for (String jobName : jobRegistry.getJobNames()) {
            restartJobs(jobName, BatchStatus.STOPPED);
        }

    }

    public void restartJobs(String jobName, BatchStatus status) throws NoSuchJobException,
            JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
            JobRestartException, JobParametersInvalidException,
            NoSuchJobInstanceException, JobExecutionAlreadyRunningException {

        // -- We are retrieving ten per ten job instances
        final int count = 10;
        for (int start = 0; ; start += count) {
            List<Long> jobInstanceIds = getJobInstances(jobName, start, count);

            LOGGER.debug("Number of jobInstanceIds={} start={} count={}.", jobInstanceIds.size(), start, count);

            if (jobInstanceIds.size() == 0) {
                return;
            }

            for (Long jobInstanceId : jobInstanceIds) {

                if (getStatus(jobInstanceId).equals(status)) {
                    restart(jobInstanceId);
                }
            }
        }
    }

    private List<Long> getExecutions(Long jobInstanceId) throws NoSuchJobInstanceException {
        JobInstance jobInstance = jobExplorer.getJobInstance(jobInstanceId);

        if (jobInstance == null) {
            throw new NoSuchJobInstanceException(String.format("No job instance with id=%d", jobInstanceId));
        }

        return jobExplorer.getJobExecutions(jobInstance)
                .stream()
                .map(JobExecution::getId)
                .collect(Collectors.toList());
    }

    @Override
    public BatchStatus getStatus(Long jobInstanceId) throws NoSuchJobInstanceException {
        BatchStatus status = BatchStatus.UNKNOWN;
        JobInstance jobInstance = jobExplorer.getJobInstance(jobInstanceId);

        if (jobInstance == null) {
            throw new NoSuchJobInstanceException(String.format("No job instance with id=%d", jobInstanceId));
        }

        for (JobExecution jobExecution : jobExplorer.getJobExecutions(jobInstance)) {
            if (jobExecution.getStatus().isGreaterThan(status)) {
                status = jobExecution.getStatus();
            }
        }

        return status;
    }

    private List<Long> getJobInstances(String jobName, int start, int count) throws NoSuchJobException {
        List<Long> list = jobExplorer.getJobInstances(jobName, start, count)
                .stream()
                .map(JobInstance::getId)
                .collect(Collectors.toList());

        if (list.isEmpty() && !jobRegistry.getJobNames().contains(jobName)) {
            throw new NoSuchJobException("No such job (either in registry or in historical data): " + jobName);
        }

        return list;
    }

    @Override
    public JobExecution start(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        return start(jobName, jobParameters, false);
    }

    public JobExecution start(String jobName, JobParameters jobParameters, boolean newInstance) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        LOGGER.info("Checking status of job with name=" + jobName);

        Job job = jobRegistry.getJob(jobName);
        JobParameters effectiveJobParameters = jobParameters;


        if (newInstance) {
            Assert.notNull(job.getJobParametersIncrementer(), "You must configure a jobParametersIncrementer for this job in order to start a new instance.");

            effectiveJobParameters = job.getJobParametersIncrementer().getNext(jobParameters);
        }

        LOGGER.info(String.format("Attempting to launch job with name=%s and parameters=%s", jobName, effectiveJobParameters.getParameters()));

        return jobLauncher.run(job, effectiveJobParameters);
    }

    @Override
    public JobExecution restart(Long jobInstanceId)
            throws JobExecutionAlreadyRunningException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobInstance jobInstance = jobExplorer.getJobInstance(jobInstanceId);
        List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

        LOGGER.info("Attempting to resume job instance with id=" + jobInstanceId);

        assert !jobExecutions.isEmpty();

        JobExecution jobExecution = jobExecutions.get(0);
        JobParameters parameters = jobExecution.getJobParameters();

        return restart(jobInstance.getJobName(), parameters);
    }

    public JobExecution restart(String jobName, JobParameters parameters)
            throws JobExecutionAlreadyRunningException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        Job job = jobRegistry.getJob(jobName);

        LOGGER.info("Attempting to resume job with name={} and parameters={}", jobName, parameters);

        return jobLauncher.run(job, parameters);
    }

    private JobExecution findExecutionById(Long jobExecutionId) throws NoSuchJobExecutionException {
        JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);

        if (jobExecution == null) {
            throw new NoSuchJobExecutionException("No JobExecution found for id: [" + jobExecutionId + "]");
        }

        return jobExecution;
    }

    @Override
    public JobExecution startNewInstance(String jobName, JobParameters jobParameters) throws NoSuchJobException,
            JobParametersInvalidException,
            JobInstanceAlreadyExistsException,
            JobExecutionAlreadyRunningException,
            JobRestartException,
            JobInstanceAlreadyCompleteException {
        return start(jobName, jobParameters, true);
    }

    @Override
    public JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
        JobExecution jobExecution = findExecutionById(jobExecutionId);
        // Indicate the execution should be stopped by setting it's status to
        // 'STOPPING'. It is assumed that
        // the step implementation will check this status at chunk boundaries.
        BatchStatus status = jobExecution.getStatus();

        if (!(status == BatchStatus.STARTED || status == BatchStatus.STARTING)) {
            throw new JobExecutionNotRunningException("JobExecution must be running so that it can be stopped: " + jobExecution);
        }

        jobExecution.setStatus(BatchStatus.STOPPING);
        jobRepository.update(jobExecution);

        return jobExecution;
    }

    @Override
    public Set<String> getJobNames() {
        return new TreeSet<>(jobRegistry.getJobNames());
    }

    @Override
    public JobExecution refresh(JobExecution jobExecution) throws NoSuchJobExecutionException {
        Assert.notNull(jobExecution);

        return findExecutionById(jobExecution.getId());
    }

    @Override
    public JobExecution abandon(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionAlreadyRunningException {
        JobExecution jobExecution = findExecutionById(jobExecutionId);

        if (jobExecution.getStatus().isLessThan(BatchStatus.STOPPING)) {
            throw new JobExecutionAlreadyRunningException(
                    "JobExecution is running or complete and therefore cannot be aborted");
        }

        LOGGER.info("Aborting job execution: " + jobExecution);

        jobExecution.upgradeStatus(BatchStatus.ABANDONED);
        jobExecution.setEndTime(new Date());
        jobRepository.update(jobExecution);

        return jobExecution;
    }

    public void setJobRegistry(JobRegistry jobRegistry) {
        this.jobRegistry = jobRegistry;
    }

    public void setJobExplorer(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }
}

