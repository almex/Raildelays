package be.raildelays.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.PropertyConfigurator;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    private static final String LOGGER_CONFIG_PATH = "./conf/log4j2.xml";

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
//        ConfigurationFactory.ConfigurationSource source = new ConfigurationFactory.ConfigurationSource(new FileInputStream(LOGGER_CONFIG_PATH));
//        LoggerContext loggerContext = Configurator.initialize(null, source);

        //-- Initialize contexts
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();
//        loggerContext.start();

        final BatchStartAndRecoveryService service = applicationContext.getBean("BatchStartAndRecoveryService", BatchStartAndRecoveryService.class);

        try {
            Properties configuration = applicationContext.getBean("configuration",
                    Properties.class);
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
            Assert.notNull(excelInputTemplate, "You must add a 'excel.input.template' property into the ./conf/raildelays.properties");
            Assert.notNull(textOutputPath, "You must add a 'text.output.path' property into the ./conf/raildelays.properties");
            Assert.notNull(excelOutputPath, "You must add a 'excel.output.path' property into the ./conf/raildelays.properties");

            if (recovery) {
                LOGGER.info("[Recovery activated]");
                service.markInconsistentJobsAsFailed();
            }

            //-- Launch one Job per date
            for (Date date : dates) {
                Map<String, JobParameter> parameters = new HashMap<>();

                parameters.put("date", new JobParameter(
                        date));
                parameters.put("station.a.name",
                        new JobParameter(departure));
                parameters.put("station.b.name",
                        new JobParameter(arrival));
                parameters.put("excel.input.template", new JobParameter(
                        excelInputTemplate));
                parameters.put("excel.output.file", new JobParameter(
                        excelOutputPath));
                parameters.put("output.file.path", new JobParameter(
                        textOutputPath));

                JobParameters jobParameters = new JobParameters(
                        parameters);

                service.start("mainJob", jobParameters);
            }
        } finally {
            if (service != null) {
                service.stopAllRunningJobs();
            }

            if (applicationContext != null) {
                applicationContext.stop();
                applicationContext.close();
            }

//            if (loggerContext != null) {
//                loggerContext.stop();
//            }
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
