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
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
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
            Properties configuration = applicationContext.getBean("configuration",
                    Properties.class);
            final DefaultJobParametersConverter converter = new DefaultJobParametersConverter();

            converter.setDateFormat(new SimpleDateFormat("dd/MM/yyyy"));

            final String departure = configuration.getProperty("departure");
            final String arrival = configuration.getProperty("arrival");
            final String excelInputTemplate = configuration.getProperty("excel.input.template");
            final String textOutputPath = configuration.getProperty("text.output.path");
            final String excelOutputPath = configuration.getProperty("excel.output.path");
            final String language = configuration.getProperty("language");
            final String mailAccountUsername = configuration.getProperty("mail.account.username");
            final String mailAccountPassword = configuration.getProperty("mail.account.password");
            final String mailServerHost = configuration.getProperty("mail.server.host");
            final String mailServerPort = configuration.getProperty("mail.server.port");
            final String mailAccountAddress = configuration.getProperty("mail.account.address");

            Assert.notNull(departure, "You must add a 'departure' property into the ./conf/raildelays.properties");
            Assert.notNull(arrival, "You must add a 'arrival' property into the ./conf/raildelays.properties");
            Assert.notNull(excelInputTemplate, "You must add a 'excel.input.template' property into the ./conf/raildelays.properties");
            Assert.notNull(textOutputPath, "You must add a 'text.output.path' property into the ./conf/raildelays.properties");
            Assert.notNull(excelOutputPath, "You must add a 'excel.output.path' property into the ./conf/raildelays.properties");
            Assert.notNull(language, "You must add a 'language' property into the ./conf/raildelays.properties");
            Assert.notNull(mailAccountUsername, "You must add a 'mail.account.username' property into the ./conf/raildelays.properties");
            Assert.notNull(mailAccountPassword, "You must add a 'mail.account.password' property into the ./conf/raildelays.properties");
            Assert.notNull(mailServerHost, "You must add a 'mail.server.host' property into the ./conf/raildelays.properties");
            Assert.notNull(mailServerPort, "You must add a 'mail.server.port' property into the ./conf/raildelays.properties");
            Assert.notNull(mailAccountAddress, "You must add a 'mail.account.address' property into the ./conf/raildelays.properties");

            if (recovery) {
                LOGGER.info("[Recovery activated]");
                service.restartAllStoppedJobs();
                service.markInconsistentJobsAsFailed();
                service.restartAllFailedJobs();
            }

            //-- Launch one Job per date
            for (Date date : dates) {
                Map<String, JobParameter> parameters = new HashMap<>();

                parameters.put("date", new JobParameter(date));
                parameters.put("station.a.name", new JobParameter(departure));
                parameters.put("station.b.name", new JobParameter(arrival));
                parameters.put("excel.input.template", new JobParameter(excelInputTemplate));
                parameters.put("excel.output.path", new JobParameter(excelOutputPath));
                parameters.put("output.file.path", new JobParameter(textOutputPath));
                parameters.put("lang", new JobParameter(language));
                parameters.put("mail.account.username", new JobParameter(mailAccountUsername));
                parameters.put("mail.account.password", new JobParameter(mailAccountPassword));
                parameters.put("mail.server.host", new JobParameter(mailServerHost));
                parameters.put("mail.server.port", new JobParameter(mailServerPort));
                parameters.put("mail.account.address", new JobParameter(mailAccountAddress));


                JobParameters jobParameters = new JobParameters(parameters);

                try {
                    service.start("mainJob", jobParameters);
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
