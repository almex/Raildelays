/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */.raildelays.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String[] contextPaths = new String[]{"/spring/bootstrap-context.xml", "/jobs/main-job-context.xml"};
        final CommandLineParser parser = new BasicParser();
        final Options options = new Options();
        final List<Date> dates;

        options.addOption("offline", false, "activate offline mode");
        options.addOption("norecovery", false, "do not execute recovery");
        options.addOption("date", false, "search delays for only on date passed as parameter");

        final CommandLine cmd = parser.parse(options, args);
        final boolean recovery = !cmd.hasOption("norecovery");
        final String searchDate = cmd.getOptionValue("date", "");

        if (StringUtils.isNotEmpty(searchDate)) {
            dates = Arrays.asList(DateUtils.parseDate(searchDate, new String[]{"dd/MM/yyyy", "dd-MM-yyyy", "yyyyMMdd"}));
        } else {
            dates = generateListOfDates();
        }

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths);

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
        } catch (Exception e) {
            LOGGER.error("Error occur in the Bootstrap", e);
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
