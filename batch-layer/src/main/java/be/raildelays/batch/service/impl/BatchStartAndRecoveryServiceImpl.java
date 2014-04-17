package be.raildelays.batch.service.impl;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.apache.commons.lang.time.DateUtils;
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

import javax.annotation.Resource;
import java.util.*;

@Service("BatchStartAndRecoveryService")
public class BatchStartAndRecoveryServiceImpl
		implements BatchStartAndRecoveryService {

	private static final String ILLEGAL_STATE_MSG = "Illegal state (only happens on a race condition): "
			+ "%s with name=%s and parameters=%s";
	
	static final private Logger LOGGER = LoggerFactory
			.getLogger(BatchStartAndRecoveryServiceImpl.class);

	@Resource
	private JobRegistry jobRegistry;

	@Resource
	private JobExplorer jobExplorer;

	@Resource
	private JobRepository jobRepository;
	
	@Resource
	private JobLauncher jobLauncher;

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

	private Set<Long> getRunningExecutions(String jobName) throws NoSuchJobException {
		Set<Long> set = new LinkedHashSet<Long>();
		
		for (JobExecution jobExecution : jobExplorer.findRunningJobExecutions(jobName)) {
			set.add(jobExecution.getId());
		}
		
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
			restartFailedJobs(jobName);
		}
	}

	public void restartFailedJobs(String jobName) throws NoSuchJobException,
			JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
			JobRestartException, JobParametersInvalidException,
			NoSuchJobInstanceException, JobExecutionAlreadyRunningException {
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

	private List<Long> getExecutions(Long jobInstanceId) throws NoSuchJobInstanceException {
		List<Long> list = new ArrayList<Long>();
		JobInstance jobInstance = jobExplorer.getJobInstance(jobInstanceId);
		
		if (jobInstance == null) {
			throw new NoSuchJobInstanceException(String.format("No job instance with id=%d", jobInstanceId));
		}
		
		for (JobExecution jobExecution : jobExplorer.getJobExecutions(jobInstance)) {
			list.add(jobExecution.getId());
		}
		
		return list;
	}

	private List<Long> getJobInstances(String jobName, int start, int count) throws NoSuchJobException {
		List<Long> list = new ArrayList<Long>();
		
		for (JobInstance jobInstance : jobExplorer.getJobInstances(jobName, start, count)) {
			list.add(jobInstance.getId());
		}
		
		if (list.isEmpty() && !jobRegistry.getJobNames().contains(jobName)) {
			throw new NoSuchJobException("No such job (either in registry or in historical data): " + jobName);
		}
		
		return list;
	}

	@Override
	public void restartAllStoppedJobs() {
		// TODO Auto-generated method stub

	}	

	@Override
	public JobExecution start(String jobName, JobParameters jobParameters) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		return start(jobName, jobParameters, false);
	}
	public JobExecution start(String jobName, JobParameters jobParameters, boolean newInstance) throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		LOGGER.info("Checking status of job with name=" + jobName);

//		if (jobRepository.isJobInstanceExists(jobName, jobParameters)) {
//			throw new JobInstanceAlreadyExistsException(String.format(
//					"Cannot start a job instance that already exists with name=%s and parameters=%s", jobName,
//					jobParameters));
//		}   //-- Useless in our case. We would like to restart it

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
	public JobExecution restart(Long jobExecutionId)
			throws JobExecutionAlreadyRunningException, NoSuchJobExecutionException, NoSuchJobException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
		LOGGER.info("Checking status of job execution with id=" + jobExecutionId);

		JobExecution jobExecution = findExecutionById(jobExecutionId);

		String jobName = jobExecution.getJobInstance().getJobName();
		Job job = jobRegistry.getJob(jobName);
		JobParameters parameters = jobExecution.getJobParameters();

		LOGGER.info(String.format("Attempting to resume job with name=%s and parameters=%s", jobName, parameters));
		
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
            JobParametersInvalidException, JobInstanceAlreadyExistsException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
		return start(jobName, jobParameters, true);
	}

	@Override
	public JobExecution stop(Long jobExecutionId) throws NoSuchJobExecutionException,
			JobExecutionNotRunningException {
		JobExecution jobExecution = findExecutionById(jobExecutionId);		
		// Indicate the execution should be stopped by setting it's status to
		// 'STOPPING'. It is assumed that
		// the step implementation will check this status at chunk boundaries.
		BatchStatus status = jobExecution.getStatus();
		
		if (!(status == BatchStatus.STARTED || status == BatchStatus.STARTING)) {
			throw new JobExecutionNotRunningException("JobExecution must be running so that it can be stopped: "+jobExecution);
		}
		
		jobExecution.setStatus(BatchStatus.STOPPING);
		jobRepository.update(jobExecution);

		return jobExecution;
	}

	@Override
	public Set<String> getJobNames() {
		return new TreeSet<String>(jobRegistry.getJobNames());
	}

	@Override
	public JobExecution abandon(Long jobExecutionId)
			throws NoSuchJobExecutionException,
			JobExecutionAlreadyRunningException {
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

}
