package be.raildelays.batch.service.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobParametersNotFoundException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import be.raildelays.batch.service.BatchStartAndRecoveryService;

@Service("BatchStartAndRecoveryService")
public class BatchStartAndRecoveryServiceImpl
		implements BatchStartAndRecoveryService {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(BatchStartAndRecoveryServiceImpl.class);

	@Resource
	private JobRegistry jobRegistry;

	@Resource
	private JobExplorer jobExplorer;

	@Resource
	private JobRepository jobRepository;

	@Resource
	private JobOperator jobOperator;

	@Override
	public void stopAllRunningJobs() {
		// TODO Auto-generated method stub

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
				LOGGER.info("Found a job already running jobExecutionId={}.",
						jobExecutionId);

				// -- Set Job Execution as FAILED
				JobExecution jobExecution = jobExplorer
						.getJobExecution(jobExecutionId);
				jobExecution.setEndTime(new Date());
				jobExecution.setStatus(BatchStatus.FAILED);
				jobExecution.setExitStatus(ExitStatus.FAILED);

				// -- Set all running Step Execution as FAILED
				for (StepExecution stepExecution : jobExecution
						.getStepExecutions()) {
					if (stepExecution.getStatus().isRunning()) {
						stepExecution.setEndTime(new Date());
						stepExecution.setStatus(BatchStatus.FAILED);
						stepExecution.setExitStatus(ExitStatus.FAILED);
					}
				}

				jobRepository.update(jobExecution);
				LOGGER.info("Setted job as FAILED!");
			}

			restartFailedJobs(jobName);
		}
	}

	@Override
	public void restartAllFailedJobs() throws NoSuchJobException,
			JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
			JobRestartException, JobParametersInvalidException,
			NoSuchJobInstanceException {
		for (String jobName : jobRegistry.getJobNames()) {
			restartFailedJobs(jobName);
		}
	}

	public void restartFailedJobs(String jobName) throws NoSuchJobException,
			JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
			JobRestartException, JobParametersInvalidException,
			NoSuchJobInstanceException {
		final Date sevenDaysBefore = DateUtils.addDays(new Date(), -7);

		// -- We are retrieving ten per ten job instances
		final int count = 10;
		for (int start = 0;; start += count) {
			List<Long> jobInstanceIds = getJobInstances(jobName, start, count);

			LOGGER.debug("Number of jobInstanceIds={} start={} count={}.",
					new Object[] { jobInstanceIds.size(), start, count });

			if (jobInstanceIds.size() == 0) {
				return;
			}

			for (Long jobInstanceId : jobInstanceIds) {
				List<Long> jobExecutionIds = getExecutions(jobInstanceId);

				LOGGER.debug("Number of jobExecutionIds={}.",
						new Object[] { jobExecutionIds.size() });

				if (jobExecutionIds.size() == 0) {
					return;
				}

				for (Long jobExecutionId : jobExecutionIds) {
					JobExecution jobExecution = jobExplorer
							.getJobExecution(jobExecutionId);

					// We will not search batch jobs older than 7 days
					if (jobExecution.getCreateTime().before(sevenDaysBefore)) {
						LOGGER.debug(
								"Last job execution analyzed for a restart : {}",
								jobExecution);
						return;
					}

					// Restartable jobs are FAILED or STOPPED
					if (jobExecution.getStatus().equals(BatchStatus.FAILED)
							|| jobExecution.getStatus().equals(
									BatchStatus.STOPPED)) {
						LOGGER.info("Restarting jobExecutionId={}...",
								jobExecutionId);
						restart(jobExecutionId);
					}
				}
			}
		}
	}

	@Override
	public void restartAllStoppedJobs() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getExecutions(long instanceId)
			throws NoSuchJobInstanceException {
		return jobOperator.getExecutions(instanceId);
	}

	@Override
	public List<Long> getJobInstances(String jobName, int start, int count)
			throws NoSuchJobException {
		return jobOperator.getJobInstances(jobName, start, count);
	}

	@Override
	public Set<Long> getRunningExecutions(String jobName)
			throws NoSuchJobException {
		return jobOperator.getRunningExecutions(jobName);
	}

	@Override
	public String getParameters(long executionId)
			throws NoSuchJobExecutionException {
		return jobOperator.getParameters(executionId);
	}

	@Override
	public Long start(String jobName, String parameters)
			throws NoSuchJobException, JobInstanceAlreadyExistsException,
			JobParametersInvalidException {
		return jobOperator.start(jobName, parameters);
	}

	@Override
	public Long restart(long executionId)
			throws JobInstanceAlreadyCompleteException,
			NoSuchJobExecutionException, NoSuchJobException,
			JobRestartException, JobParametersInvalidException {
		return jobOperator.restart(executionId);
	}

	@Override
	public Long startNextInstance(String jobName) throws NoSuchJobException,
			JobParametersNotFoundException, JobRestartException,
			JobExecutionAlreadyRunningException,
			JobInstanceAlreadyCompleteException,
			UnexpectedJobExecutionException, JobParametersInvalidException {
		return jobOperator.startNextInstance(jobName);
	}

	@Override
	public boolean stop(long executionId) throws NoSuchJobExecutionException,
			JobExecutionNotRunningException {
		return jobOperator.stop(executionId);
	}

	@Override
	public String getSummary(long executionId)
			throws NoSuchJobExecutionException {
		return jobOperator.getSummary(executionId);
	}

	@Override
	public Map<Long, String> getStepExecutionSummaries(long executionId)
			throws NoSuchJobExecutionException {
		return jobOperator.getStepExecutionSummaries(executionId);
	}

	@Override
	public Set<String> getJobNames() {
		return jobOperator.getJobNames();
	}

	@Override
	public JobExecution abandon(long jobExecutionId)
			throws NoSuchJobExecutionException,
			JobExecutionAlreadyRunningException {
		return jobOperator.abandon(jobExecutionId);
	}

}
