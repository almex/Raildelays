package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
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
import be.raildelays.service.RaildelaysService;

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
		SimpleDateFormat formater = new SimpleDateFormat("dd/mm/yyyy");
		// String[] trainIds = new String[] { "466", "467", "468", "514",
		// "515", "516", "477", "478", "479", "529", "530", "531" };
		Calendar oneWeekBefore = Calendar.getInstance();
		oneWeekBefore.add(Calendar.DAY_OF_MONTH, -7);
		oneWeekBefore = DateUtils.truncate(oneWeekBefore,
				Calendar.DAY_OF_MONTH);
		Iterator<?> iterator = DateUtils.iterator(oneWeekBefore,
				DateUtils.RANGE_WEEK_RELATIVE);
		
		

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				contextPaths);
		ctx.start();

		try {
			RaildelaysService service = ctx.getBean(RaildelaysService.class);

			if (args.length == 0) {
				JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
				JobRegistry jobRegistry = ctx.getBean(JobRegistry.class);
				JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
				JobOperator jobOperator = ctx.getBean(JobOperator.class);
				JobRepository jobRepository = ctx.getBean(JobRepository.class);
				recover(jobExplorer, jobRepository, jobOperator);
				while (iterator.hasNext()) {
					Calendar calendar = (Calendar) iterator.next();
					//				Map<String, JobParameter> parameters = new HashMap<>();
					//				JobParameter jobParameter = new JobParameter(calendar.getTime());
					//
					//				parameters.put("date", jobParameter);

					jobOperator.start("grabDelaysFromRailtime", "date="
							+ formater.format(calendar.getTime()));
				}
			}
			LOGGER.debug("Searching delays...");
			
			iterator = DateUtils.iterator(oneWeekBefore,
					DateUtils.RANGE_WEEK_RELATIVE);
			
			while (iterator.hasNext()) {
				Calendar calendar = (Calendar) iterator.next();
				Set<LineStop> stops = new HashSet<LineStop>();
				Station sationA = new Station("Liège-Guillemins");
				Station stationB = new Station("Brussels (Bruxelles)-Central");

				stops.addAll(service.searchDelaysBetween(calendar.getTime(),
						sationA, stationB, 15));

				print(stops, sationA, stationB);
			}

		} finally {
			if (ctx != null) {
				ctx.stop();
				ctx.close();
			}
		}
	}

	private static Long recover(JobExplorer jobExplorer, JobRepository jobRepository, JobOperator jobOperator)
			throws NoSuchJobException, NoSuchJobExecutionException,
			JobExecutionNotRunningException, InterruptedException,
			JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException, JobParametersInvalidException {
		Set<Long> jobExecutionIds = jobOperator
				.getRunningExecutions("grabDelaysFromRailtime");

		Long lastJobExectutionId = null;
		for (Long jobExecutionId : jobExecutionIds) {
			LOGGER.debug("Found a job already running jobExecutionId={}",
					jobExecutionId);

			JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
			jobExecution.setEndTime(new Date());
			jobExecution.setStatus(BatchStatus.FAILED);
			jobExecution.setExitStatus(ExitStatus.FAILED);
			
			for(StepExecution stepExecution : jobExecution.getStepExecutions()) {
				if (stepExecution.getStatus().isRunning()) {
					stepExecution.setEndTime(new Date());
					stepExecution.setStatus(BatchStatus.FAILED);
					stepExecution.setExitStatus(ExitStatus.FAILED);
				}
			}
				
			jobRepository.update(jobExecution);
			System.out.println("Setted job as FAILED!");
			
			lastJobExectutionId = jobExecutionId;
			
			LOGGER.debug("Restarting jobExecutionId={}...",
					lastJobExectutionId);
			jobOperator.restart(lastJobExectutionId);
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
