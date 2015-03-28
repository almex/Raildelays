package be.raildelays.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
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
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.*;

public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String[] contextPaths = new String[]{"/spring/bootstrap-context.xml"};
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
            dates = Arrays.asList(DateUtils.parseDate(searchDate, new String[]{"dd/MM/yyyy", "dd-MM-yyyy", "yyyyMMdd"}));
        } else {
            dates = generateListOfDates();
        }

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                contextPaths);

        //-- Initialize contexts
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        final BatchStartAndRecoveryService service = applicationContext.getBean("BatchStartAndRecoveryService", BatchStartAndRecoveryService.class);

        try {
            JobParametersExtractor propertiesExtractor = applicationContext.getBean("jobParametersFromPropertiesExtractor",
                    JobParametersExtractor.class);


            if (recovery) {
                LOGGER.info("[Recovery activated]");
                service.restartAllStoppedJobs();
                service.markInconsistentJobsAsFailed();
                service.restartAllFailedJobs();
            }

            //-- Launch one Job per date
            for (Date date : dates) {
                JobParameters jobParameters = propertiesExtractor.getJobParameters(null, null);
                JobParametersBuilder builder = new JobParametersBuilder(jobParameters);


                builder.addDate("date", date);

                try {
                    service.start("mainJob", builder.toJobParameters());
                } catch (JobInstanceAlreadyCompleteException e) {
                    LOGGER.warn("Job already completed for this date {}", new SimpleDateFormat("dd/MM/yyyy").format(date));
                }
            }
        } finally {
            if (service != null) {
                service.stopAllRunningJobs();
            }

            if (applicationContext != null) {
                applicationContext.stop();
                applicationContext.close();
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

        Collections.sort(result); //-- To order outcomes

        return result;
    }

}
