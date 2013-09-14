package be.raildelays.batch.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;


public interface BatchStartAndRecoveryService {

		/**
		 * Stop all running jobs (STARTING, STARTED, STOPPING)
		 */
		void stopAllRunningJobs();
		
		/**
		 * This method must be call before any start of a job to recover
		 * inconsitency within batch job repository due to an unproper shutdown.
		 * 
		 * @param jobRegistry
		 * @param jobExplorer
		 * @param jobRepository
		 * @param jobOperator
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
		
		void restartAllFailedJobs() throws NoSuchJobException, JobInstanceAlreadyCompleteException, NoSuchJobExecutionException, JobRestartException, JobParametersInvalidException, NoSuchJobInstanceException;
		
		void restartAllStoppedJobs();

		List<Long> getExecutions(long instanceId)
				throws NoSuchJobInstanceException;

		List<Long> getJobInstances(String jobName, int start, int count)
				throws NoSuchJobException;

		Set<Long> getRunningExecutions(String jobName)
				throws NoSuchJobException;

		String getParameters(long executionId)
				throws NoSuchJobExecutionException;

		JobExecution run(String jobName, JobParameters parameters)
			throws JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, JobParametersInvalidException, NoSuchJobException;

		Long restart(long executionId)
				throws JobInstanceAlreadyCompleteException,
				NoSuchJobExecutionException, NoSuchJobException,
				JobRestartException, JobParametersInvalidException;

		Long startNextInstance(String jobName) throws NoSuchJobException,
				JobParametersNotFoundException, JobRestartException,
				JobExecutionAlreadyRunningException,
				JobInstanceAlreadyCompleteException,
				UnexpectedJobExecutionException, JobParametersInvalidException;

		boolean stop(long executionId) throws NoSuchJobExecutionException,
				JobExecutionNotRunningException;

		String getSummary(long executionId) throws NoSuchJobExecutionException;

		Map<Long, String> getStepExecutionSummaries(long executionId)
				throws NoSuchJobExecutionException;

		Set<String> getJobNames();

		JobExecution abandon(long jobExecutionId)
				throws NoSuchJobExecutionException,
				JobExecutionAlreadyRunningException;
	
}
