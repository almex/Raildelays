package be.raildelays.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import be.raildelays.batch.service.BatchStartAndRecoveryService;

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
			final BatchStartAndRecoveryService service = ctx.getBean(BatchStartAndRecoveryService.class);
			final FlatFileItemReader<String> trainListReader = ctx.getBean("trainList", FlatFileItemReader.class);
			final DefaultJobParametersConverter converter = new DefaultJobParametersConverter();
			
			converter.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));

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
			
			if (recovery) {
				LOGGER.info("[Recovery activated]");
				service.markInconsistentJobsAsFailed();
				service.restartAllFailedJobs();
				service.restartAllStoppedJobs();
			}

			//-- Search all delays from Railtime
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

									service.start("retrieveDataFromRailtimeJob", jobParameters.toString());
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

			//-- Generate simple flat file
			if (StringUtils.isNotEmpty(textOutputPath)) {
				Map<String, JobParameter> parameters = new HashMap<>();

				parameters.put("date", new JobParameter(new Date()));
				parameters.put("station.a.name", new JobParameter(departure));
				parameters.put("station.b.name", new JobParameter(arrival));
				parameters.put("output.file.path", new JobParameter(
						textOutputPath));

				JobParameters jobParameters = new JobParameters(parameters);

				service.start("searchDelaysJob", jobParameters.toString());
			}
			
			//-- Generate Excel sheets
			if (StringUtils.isNotEmpty(excelOutputPath)) {
				Map<String, JobParameter> parameters = new HashMap<>();

				parameters.put("date", new JobParameter(new Date()));
				parameters.put("station.a.name", new JobParameter(departure));
				parameters.put("station.b.name", new JobParameter(arrival));
				parameters.put("excel.input.template", new JobParameter(
						excelInputTemplate));
				parameters.put("excel.output.file", new JobParameter(
						excelOutputPath));

				JobParameters jobParameters = new JobParameters(parameters);
				

				service.start("searchDelaysXlsJob", jobParameters.toString());
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

}
