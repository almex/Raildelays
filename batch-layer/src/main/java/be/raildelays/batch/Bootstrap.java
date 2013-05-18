package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(Bootstrap.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String[] contextPaths = new String[] { "/spring/bootstrap-context.xml" };
		List<Date> dates = generateListOfDates();
		CommandLineParser parser = new BasicParser();
		Options options = new Options();

		options.addOption("offline", false, "activate offline mode");
		options.addOption("norecovery", false, "do not execute recovery");

		CommandLine cmd = parser.parse(options, args);
		boolean online = !cmd.hasOption("offline");
		boolean recovery = !cmd.hasOption("norecovery");
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				contextPaths);

		ctx.start();

		try {
			Properties configuration = ctx.getBean("configuration",
					Properties.class);
			JobRegistry jobRegistry = ctx.getBean(JobRegistry.class);
			JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
			JobOperator jobOperator = ctx.getBean(JobOperator.class);
			JobRepository jobRepository = ctx.getBean(JobRepository.class);
			JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
			DefaultJobParametersConverter converter = new DefaultJobParametersConverter();
			converter.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
			Job retrieveDataFromRailtimeJob = null;
			Job searchDelaysJob = null;
			Job searchDelaysXlsJob = null;

			String departure = configuration.getProperty("departure");
			String arrival = configuration.getProperty("arrival");
			String excelInputTemplate = configuration
					.getProperty("excel.input.template");
			String textOutputPath = configuration
					.getProperty("text.output.path");
			String excelOutputPath = configuration
					.getProperty("excel.output.path");

			LOGGER.info("jobNames={}", jobRegistry.getJobNames());

			if (online) {
				LOGGER.info("[ON-line mode activated]");
				retrieveDataFromRailtimeJob = jobRegistry
						.getJob("retrieveDataFromRailtimeJob");
			} else {
				LOGGER.info("[OFF-line mode activated]");
			}

			searchDelaysJob = jobRegistry.getJob("searchDelaysJob");
			searchDelaysXlsJob = jobRegistry.getJob("searchDelaysXlsJob");

			if (recovery) {
				LOGGER.info("[Recovery activated]");
				recover(jobRegistry, jobExplorer, jobRepository, jobOperator);
			}

			if (retrieveDataFromRailtimeJob != null) {
				for (Date date : dates) {
					Map<String, JobParameter> parameters = new HashMap<>();

					parameters.put("input.file.path", new JobParameter(
							"file:./conf/train.list"));
					parameters.put("date", new JobParameter(date));
					parameters.put("station.a.name",
							new JobParameter(departure));
					parameters.put("station.b.name", new JobParameter(arrival));

					JobParameters jobParameters = new JobParameters(parameters);

					startOrRestartJob(jobLauncher, retrieveDataFromRailtimeJob,
							jobParameters, converter);
				}
			}

			if (searchDelaysJob != null) {
				Map<String, JobParameter> parameters = new HashMap<>();

				parameters.put("date", new JobParameter(new Date()));
				parameters.put("station.a.name", new JobParameter(departure));
				parameters.put("station.b.name", new JobParameter(arrival));
				parameters.put("output.file.path", new JobParameter(
						textOutputPath));
				parameters.put("excel.input.template", new JobParameter(
						excelInputTemplate));
				parameters.put("excel.output.file", new JobParameter(
						excelOutputPath));

				JobParameters jobParameters = new JobParameters(parameters);

				startOrRestartJob(jobLauncher, searchDelaysJob, jobParameters,
						converter);

				// startOrRestartJob(jobLauncher, searchDelaysXlsJob,
				// jobParameters,
				// converter);
			}
		} finally {
			if (ctx != null) {
				ctx.stop();
				ctx.close();
			}
		}

		System.exit(0);
	}

	/**
	 * Generate a list of day of week from today to 7 in the past.
	 * 
	 * @return a list of {@link Date} from Monday to Friday.
	 */
	private static List<Date> generateListOfDates() {
		List<Date> result = new ArrayList<>();
		Calendar date = DateUtils.truncate(Calendar.getInstance(),
				Calendar.DAY_OF_MONTH);
		date.setLenient(false);
		Date monday = null;
		Date tuesday = null;
		Date wednesday = null;
		Date thursday = null;
		Date friday = null;

		for (int i = -8; i < 0; i++) {
			date.add(Calendar.DAY_OF_MONTH, -1);

			switch (date.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.MONDAY:
				monday = date.getTime();
				break;
			case Calendar.TUESDAY:
				tuesday = date.getTime();
				break;
			case Calendar.WEDNESDAY:
				wednesday = date.getTime();
				break;
			case Calendar.THURSDAY:
				thursday = date.getTime();
				break;
			case Calendar.FRIDAY:
				friday = date.getTime();
				break;
			default:
				break;
			}
		}

		result.add(monday);
		result.add(tuesday);
		result.add(wednesday);
		result.add(thursday);
		result.add(friday);

		return result;
	}

	/**
	 * Start or restart a batch Job
	 * 
	 * @param jobLauncher
	 *            job launcher
	 * @param job
	 *            to execute
	 * @param parameters
	 *            parameters for this job instance
	 * @param converter
	 *            to convert job parameters
	 * @throws JobParametersInvalidException
	 *             thrown when a parameter conversion failed
	 */
	public static void startOrRestartJob(final JobLauncher jobLauncher,
			final Job job, final JobParameters jobParameters,
			final JobParametersConverter converter)
			throws JobParametersInvalidException {
		try {
			LOGGER.info("Starting {}...", job.getName());
			jobLauncher.run(job, jobParameters);
		} catch (JobExecutionAlreadyRunningException e) {
			LOGGER.info("{} is already running!", job.getName());
			LOGGER.error("Exception:", e);
		} catch (JobInstanceAlreadyCompleteException e) {
			LOGGER.info("{} is already complete!", job.getName());
			LOGGER.error("Exception:", e);
		} catch (JobRestartException e) {
			LOGGER.info("Unexpected restart exception");
			LOGGER.error("Exception:", e);
		}
	}

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
	private static void recover(JobRegistry jobRegistry,
			JobExplorer jobExplorer, JobRepository jobRepository,
			JobOperator jobOperator) throws NoSuchJobException,
			NoSuchJobExecutionException, JobExecutionNotRunningException,
			InterruptedException, JobExecutionAlreadyRunningException,
			JobInstanceAlreadyCompleteException, JobRestartException,
			JobParametersInvalidException, NoSuchJobInstanceException {
		Collection<String> jobNames = jobRegistry.getJobNames();

		for (String jobName : jobNames) {
			LOGGER.info("Searching to recover jobName={}...", jobName);

			// -- Retrieve all jobs marked as STARTED or STOPPING
			Set<Long> jobExecutionIds = jobOperator
					.getRunningExecutions(jobName);

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

			restartFailedJobs(jobExplorer, jobOperator, jobName);
		}
	}

	private static void restartFailedJobs(JobExplorer jobExplorer,
			JobOperator jobOperator, String jobName) throws NoSuchJobException,
			JobInstanceAlreadyCompleteException, NoSuchJobExecutionException,
			JobRestartException, JobParametersInvalidException,
			NoSuchJobInstanceException {
		final Date sevenDaysBefore = DateUtils.addDays(new Date(), -7);

		// -- We are retrieving ten per ten job instances
		final int count = 10;
		for (int start = 0;; start += count) {
			List<Long> jobInstanceIds = jobOperator.getJobInstances(jobName,
					start, count);

			LOGGER.debug("Number of jobInstanceIds={} start={} count={}.",
					new Object[] { jobInstanceIds.size(), start, count });

			if (jobInstanceIds.size() == 0) {
				return;
			}

			for (Long jobInstanceId : jobInstanceIds) {
				List<Long> jobExecutionIds = jobOperator
						.getExecutions(jobInstanceId);

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
						jobOperator.restart(jobExecutionId);
					}
				}
			}
		}
	}

}
