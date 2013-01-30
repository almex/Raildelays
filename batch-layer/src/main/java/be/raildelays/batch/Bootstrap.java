package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

public class Bootstrap {

	static final private Logger LOGGER = LoggerFactory
			.getLogger(Bootstrap.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String[] contextPaths = new String[] {
				"/spring/batch/raildelays-batch-integration-context.xml",
				"/jobs/batch-jobs-context.xml" };
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		Calendar oneWeekBefore = Calendar.getInstance();
		oneWeekBefore.add(Calendar.DAY_OF_MONTH, -7);
		oneWeekBefore = DateUtils
				.truncate(oneWeekBefore, Calendar.DAY_OF_MONTH);
		Iterator<?> iterator = DateUtils.iterator(oneWeekBefore,
				DateUtils.RANGE_WEEK_RELATIVE);

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				contextPaths);
		ctx.start();

		try {

			if (args.length == 0) {
				JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
				JobRegistry jobRegistry = ctx.getBean(JobRegistry.class);
				JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
				JobOperator jobOperator = ctx.getBean(JobOperator.class);
				JobRepository jobRepository = ctx.getBean(JobRepository.class);
				Job job = ctx.getBean(Job.class);
				
				recover(jobRegistry, jobExplorer, jobRepository, jobOperator);
				
				while (iterator.hasNext()) {
					Calendar calendar = (Calendar) iterator.next();
					jobOperator
							.start(job.getName(),
									"input.file.path=train-list.properties,"
											+ "date="
											+ formater.format(calendar
													.getTime())
											+ ","
											+ "station.a.name=Liège-Guillemins,"
											+ "station.b.name=Brussels (Bruxelles)-Central,"
											+ "output.file.path=file:./output.dat");
				}
			}
			/*
			 * RaildelaysService service = ctx.getBean(RaildelaysService.class);
			 * LOGGER.debug("Searching delays...");
			 * 
			 * iterator = DateUtils.iterator(oneWeekBefore,
			 * DateUtils.RANGE_WEEK_RELATIVE);
			 * 
			 * while (iterator.hasNext()) { Calendar calendar = (Calendar)
			 * iterator.next(); Set<LineStop> stops = new HashSet<LineStop>();
			 * Station sationA = new Station("Liège-Guillemins"); Station
			 * stationB = new Station("Brussels (Bruxelles)-Central");
			 * 
			 * stops.addAll(service.searchDelaysBetween(calendar.getTime(),
			 * sationA, stationB, 15));
			 * 
			 * print(stops, sationA, stationB); }
			 */

		} finally {
			if (ctx != null) {
				ctx.stop();
				ctx.close();
			}
		}

		System.exit(0);
	}

	private static Long recover(JobRegistry jobRegistry,
			JobExplorer jobExplorer, JobRepository jobRepository,
			JobOperator jobOperator) throws NoSuchJobException,
			NoSuchJobExecutionException, JobExecutionNotRunningException,
			InterruptedException, JobExecutionAlreadyRunningException,
			JobInstanceAlreadyCompleteException, JobRestartException,
			JobParametersInvalidException {
		Collection<String> jobNames = jobRegistry.getJobNames();

		Long lastJobExectutionId = null;
		for (String jobName : jobNames) {
			System.out.println("Searching to recover jobName=" + jobName);

			Set<Long> jobExecutionIds = jobOperator
					.getRunningExecutions(jobName);

			lastJobExectutionId = null;
			for (Long jobExecutionId : jobExecutionIds) {
				System.out
						.println("Found a job already running jobExecutionId="
								+ lastJobExectutionId + "...");

				JobExecution jobExecution = jobExplorer
						.getJobExecution(jobExecutionId);
				jobExecution.setEndTime(new Date());
				jobExecution.setStatus(BatchStatus.FAILED);
				jobExecution.setExitStatus(ExitStatus.FAILED);

				for (StepExecution stepExecution : jobExecution
						.getStepExecutions()) {
					if (stepExecution.getStatus().isRunning()) {
						stepExecution.setEndTime(new Date());
						stepExecution.setStatus(BatchStatus.FAILED);
						stepExecution.setExitStatus(ExitStatus.FAILED);
					}
				}

				jobRepository.update(jobExecution);
				System.out.println("Setted job as FAILED!");

				lastJobExectutionId = jobExecutionId;

				System.out.println("Restarting jobExecutionId="
						+ jobExecutionId + "...");

				jobOperator.restart(jobExecutionId);
			}
		}

		return lastJobExectutionId;
	}

	private static void print(Collection<LineStop> stops, Station departure,
			Station arrival) {
		for (LineStop stop : stops) {
			LOGGER.info(stop.toStringAll());
			LOGGER.info("=================================");
		}
	}

}
