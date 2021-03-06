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

package be.raildelays.batch.service;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.List;


public interface BatchStartAndRecoveryService {

    /**
     * Stop all running jobs (STARTING, STARTED, STOPPING)
     * @return all {@link JobExecution} which have been stopped
     */
    List<JobExecution> stopAllRunningJobs() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException;

    /**
     * This method must be call before any start of a job to recover
     * inconsistency within batch job repository due, for instance, to an abrupt shutdown.
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
     * @return all {@link JobExecution} which have been marked as failed
     */
    List<JobExecution> markInconsistentJobsAsFailed() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException, InterruptedException, JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException;

    /**
     * Restart all job that have the {@link BatchStatus#STOPPED}.
     *
     * @return the list of {@link JobExecution} which have been restarted
     * @throws NoSuchJobException
     * @throws JobInstanceAlreadyCompleteException
     * @throws NoSuchJobExecutionException
     * @throws JobRestartException
     * @throws JobParametersInvalidException
     * @throws NoSuchJobInstanceException
     * @throws JobExecutionAlreadyRunningException
     */
    List<JobExecution> restartAllFailedJobs() throws NoSuchJobException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException, JobExecutionAlreadyRunningException;

    /**
     * Restart all job that have the {@link BatchStatus#FAILED}.
     *
     * @return the list of {@link JobExecution} which have been restarted
     * @throws NoSuchJobException
     * @throws JobInstanceAlreadyCompleteException
     * @throws NoSuchJobExecutionException
     * @throws JobRestartException
     * @throws JobParametersInvalidException
     * @throws NoSuchJobInstanceException
     * @throws JobExecutionAlreadyRunningException
     */
    List<JobExecution> restartAllStoppedJobs() throws NoSuchJobException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException, JobExecutionAlreadyRunningException;

    JobExecution start(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    JobExecution restart(Long jobExecutionId) throws JobExecutionAlreadyRunningException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException;

    JobExecution startNewInstance(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException;

    JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException;

    JobExecution abandon(Long jobExecutionId) throws NoSuchJobExecutionException, JobExecutionAlreadyRunningException;

    List<String> getJobNames();

    BatchStatus getStatus(Long jobInstanceId) throws NoSuchJobInstanceException;

    JobExecution refresh(JobExecution jobExecution) throws NoSuchJobExecutionException;

}
