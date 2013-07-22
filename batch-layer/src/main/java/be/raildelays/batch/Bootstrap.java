package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.callback.NestedRepeatCallback;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

public class Bootstrap {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(Bootstrap.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final String[] contextPaths = new String[] { "/spring/bootstrap-context.xml" };
		final CommandLineParser parser = new BasicParser();
		final Options options = new Options();
		final List<Date> dates;

		options.addOption("offline", false, "activate offline mode");
		options.addOption("norecovery", false, "do not execute recovery");
		options.addOption("date", false, "search delays for only on date passed as parameter");

		final CommandLine cmd = parser.parse(options, args);
		final boolean online = !cmd.hasOption("offline");
		final boolean recovery = !cmd.hasOption("norecovery");
		final String searchDate = cmd.getOptionValue("date", "");
		
		if (StringUtils.isNotEmpty(searchDate)) {
			dates = Arrays.asList(DateUtils.parseDate(searchDate, new String[] {"dd/MM/yyyy", "dd-MM-yyyy", "yyyyMMdd"}));
		} else {
			dates = generateListOfDates();
		}
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				contextPaths);

		ctx.start();

		try {
			Properties configuration = ctx.getBean("configuration",
					Properties.class);
			final JobRegistry jobRegistry = ctx.getBean(JobRegistry.class);
			final JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
			final JobOperator jobOperator = ctx.getBean(JobOperator.class);
			final JobRepository jobRepository = ctx.getBean(JobRepository.class);
			final JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
			final FlatFileItemReader<String> trainListReader = ctx.getBean("trainList", FlatFileItemReader.class);
			final DefaultJobParametersConverter converter = new DefaultJobParametersConverter();
			
			converter.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));
			
			final Job retrieveDataFromRailtimeJob = jobRegistry
					.getJob("retrieveDataFromRailtimeJob");;
			final Job searchDelaysJob = jobRegistry.getJob("searchDelaysJob");
			final Job searchDelaysXlsJob = jobRegistry.getJob("searchDelaysXlsJob");

			final String departure = configuration.getProperty("departure");
			final String arrival = configuration.getProperty("arrival");
			final String excelInputTemplate = configuration
					.getProperty("excel.input.template");
			final String textOutputPath = configuration
					.getProperty("text.output.path");
			final String excelOutputPath = configuration
					.getProperty("excel.output.path");
			
			Assert.notNull(departure, "You must add a 'departure' property into the ./conf/raildelays.properties");
			Assert.notNull(arrival, "You must add a 'arrival' property into the ./conf/raildelays.properties");
			Assert.notNull(excelInputTemplate, "You must add a 'excel.input.template' property into the ./conf/raildelays.properties");
			Assert.notNull(textOutputPath, "You must add a 'text.output.path' property into the ./conf/raildelays.properties");
			Assert.notNull(excelOutputPath, "You must add a 'excel.output.path' property into the ./conf/raildelays.properties");

			LOGGER.info("jobNames={}", jobRegistry.getJobNames());

			if (recovery) {
				LOGGER.info("[Recovery activated]");
				recover(jobRegistry, jobExplorer, jobRepository, jobOperator);
			}

			if (online) {
				LOGGER.info("[ON-line mode activated]");
				
				try {
					trainListReader.open(new ExecutionContext());
					RepeatTemplate template = new RepeatTemplate();
					template.iterate(new RepeatCallback() {

					    public RepeatStatus doInIteration(RepeatContext context) throws UnexpectedInputException, ParseException, Exception {
					    	RepeatStatus result = RepeatStatus.CONTINUABLE;
					        String trainId = trainListReader.read();
					        
					        if (trainId != null) {
								for (Date date : dates) {
									Map<String, JobParameter> parameters = new HashMap<>();

									parameters.put("date", new JobParameter(
											date));
									parameters.put("trainId", new JobParameter(
											trainId));
									parameters.put("station.a.name",
											new JobParameter(departure));
									parameters.put("station.b.name",
											new JobParameter(arrival));

									JobParameters jobParameters = new JobParameters(
											parameters);

									startOrRestartJob(jobLauncher,
											retrieveDataFromRailtimeJob,
											jobParameters, converter);
								}
							} else {
								result = RepeatStatus.FINISHED;
							}
					        
							return result;
					    }

					});
				} catch (Exception e) {
					// TODO: handle exception
				}
				finally {
					trainListReader.close();
				}
			} else {
				LOGGER.info("[OFF-line mode activated]");
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

		for (int i = 0; i < 7; i++) {
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
