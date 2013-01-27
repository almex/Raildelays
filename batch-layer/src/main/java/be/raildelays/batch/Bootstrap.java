package be.raildelays.batch;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.httpclient.RequestStreamer;
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
				"/spring/batch/raildelays-batch-integration-context.xml"/*,
				"/jobs/batch-jobs-context.xml"*/ };

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				contextPaths);
		ctx.start();

		try {
			RaildelaysService service = ctx.getBean(RaildelaysService.class);
			JobLauncher jobLauncher = ctx.getBean(JobLauncher.class);
			JobRegistry jobRegistry = ctx.getBean(JobRegistry.class);
			JobExplorer jobExplorer = ctx.getBean(JobExplorer.class);
			JobOperator jobOperator = ctx.getBean(JobOperator.class);
			RequestStreamer streamer = ctx.getBean(RequestStreamer.class);
			// String[] trainIds = new String[] { "466", "467", "468", "514",
			// "515", "516", "477", "478", "479", "529", "530", "531" };
			Calendar oneWeekBefore = Calendar.getInstance();
			oneWeekBefore.add(Calendar.DAY_OF_MONTH, -7);
			oneWeekBefore = DateUtils.truncate(oneWeekBefore,
					Calendar.DAY_OF_MONTH);

			Iterator<?> iterator = DateUtils.iterator(oneWeekBefore,
					DateUtils.RANGE_WEEK_RELATIVE);

			while (iterator.hasNext()) {
				Calendar calendar = (Calendar) iterator.next();
				Set<LineStop> stops = new HashSet<LineStop>();
				Station sationA = new Station("Liège-Guillemins");
				Station stationB = new Station("Brussels (Bruxelles)-Central");

				// for (String idTrain : Arrays.asList(trainIds)) {
				//
				// }

				Map<String, JobParameter> parameters = new HashMap<>();
				JobParameter jobParameter = new JobParameter(calendar.getTime());

				parameters.put("date", jobParameter);

				// try {
				// jobLauncher.run(
				// jobRegistry.getJob("grabDelaysFromRailtime"),
				// new JobParameters(parameters));
				// } catch (JobExecutionAlreadyRunningException e) {
				// String message = e.getMessage();
				// int deginIndex = e.getMessage().indexOf("id=");
				// int endIndex = e.getMessage().indexOf(", version=");
				// String id = message.substring(deginIndex+3, endIndex);
				//
				// Long instanceId = Long.parseLong(id);
				//
				// jobOperator.start("grabDelaysFromRailtime", parameters);
				//
				// jobLauncher.run(
				// jobRegistry.getJob(),
				// new JobParameters(parameters));
				// }
				Set<Long> jobExecutionIds = jobOperator
						.getRunningExecutions("grabDelaysFromRailtime");

				if (jobExecutionIds.isEmpty()) {
					jobLauncher.run(
							jobRegistry.getJob("grabDelaysFromRailtime"),
							new JobParameters(parameters));
					// jobOperator.start("grabDelaysFromRailtime", "date="
					// + jobParameter.getValue());
				} else {

					Long lastJobExectutionId = null;
					for (Long jobExecutionId : jobExecutionIds) {
						// Long jobExecutionId = jobExecutionIds.toArray(new
						// Long[]{})[0];
						LOGGER.debug(
								"Found a job already running jobExecutionId={}",
								jobExecutionId);
						jobOperator.stop(jobExecutionId);
						System.out.print("Stopping job");
						boolean stopped = false;
						for (int i = 0; i < 30; i++) {
							System.out.print('.');
							Thread.sleep(1000);
							stopped = jobExplorer.getJobExecution(
									jobExecutionId).getEndTime() != null;
							if (stopped) {
								break;
							}
						}

						System.out.println(" DONE!");

						if (stopped) {
							System.out.println("Job stopped!");
						} else {
							jobOperator.abandon(jobExecutionId);
							System.out.println("Job abandonned!");
						}

						lastJobExectutionId = jobExecutionId;
					}

					LOGGER.debug("Restarting jobExecutionId={}",
							lastJobExectutionId);
					jobOperator.restart(lastJobExectutionId);
				}

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

	private static void print(Collection<LineStop> stops, Station departure,
			Station arrival) {
		for (LineStop stop : stops) {
			System.out.println(stop.toStringAll());
			System.out.println("=================================");
		}
	}

}
