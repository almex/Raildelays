package be.raildelays.batch.listener;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;

import be.raildelays.batch.service.BatchStartAndRecoveryService;

/**
 * This class listens to events from the Operating System requesting the Batch
 * to shutdown. For example when the user hits CTRL-C or the system is shutting
 * down. If we ignore these signals the jobs will be left hanging. This class
 * attempts to remedy this situation by requesting the JobOperator to gracefully
 * stop a job when the JVM calls the shutdown hook.
 */
public class ProcessShutdownListener implements JobExecutionListener {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProcessShutdownListener.class);

	@Resource
	private BatchStartAndRecoveryService jobOperator;

	@Override
	public void afterJob(JobExecution jobExecution) { /* do nothing. */
	}

	@Override
	public void beforeJob(final JobExecution jobExecution) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					jobOperator.stop(jobExecution.getId());
					while (jobExecution.isRunning()) {
						LOGGER.info("Waiting for job to stop...");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							LOGGER.info("Process interupted before the end", e);
						}
					}
				} catch (NoSuchJobExecutionException e) {
					LOGGER.error("Job repository is inconsistent", e);
				} catch (JobExecutionNotRunningException e) {
					LOGGER.error("In rare condition, job can have finished just after checking it was running", e);
				}
			}
		});
	}

	public void setJobOperator(BatchStartAndRecoveryService jobOperator) {
		this.jobOperator = jobOperator;
	}

}
