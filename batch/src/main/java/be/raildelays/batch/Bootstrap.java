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
 */

package be.raildelays.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Almex
 * @since 1.0
 */
public class Bootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    private Bootstrap() {
        // N/A for a main() method
    }

    /**
     * @param args offline: activate offline mode, norecovery: do not execute recovery, date: search delays for only
     *             on date passed as parameter
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final String[] contextPaths = new String[]{"/spring/bootstrap-context.xml", "/jobs/main-job-context.xml"};
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();
        final List<LocalDate> dates;

        options.addOption("offline", false, "activate offline mode");
        options.addOption("norecovery", false, "do not execute recovery");
        options.addOption("date", false, "search delays for only on date passed as parameter");

        final CommandLine cmd = parser.parse(options, args);
        final boolean recovery = !cmd.hasOption("norecovery");
        final String searchDate = cmd.getOptionValue("date", "");

        if (StringUtils.isNotEmpty(searchDate)) {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .append(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    .append(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    .append(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    .toFormatter();

            dates = Collections.singletonList(LocalDate.parse(searchDate, formatter));
        } else {
            dates = ExcelFileUtils.generateListOfDates();
        }

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths);

        //-- Initialize contexts
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        BatchStartAndRecoveryService service = applicationContext.getBean(
                "batchStartAndRecoveryService", BatchStartAndRecoveryService.class
        );

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
            for (LocalDate date : dates) {
                startMainJob(service, propertiesExtractor, date);
            }
        } catch (Exception e) {
            LOGGER.error("Error occur in the Bootstrap", e);
        } finally {
            if (service != null) {
                service.stopAllRunningJobs();
            }

            applicationContext.stop();
            applicationContext.close();
        }

        System.exit(0);
    }

    private static void startMainJob(BatchStartAndRecoveryService service,
                                     JobParametersExtractor propertiesExtractor,
                                     LocalDate date)
            throws JobInstanceAlreadyExistsException, NoSuchJobException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException {
        JobParameters jobParameters = propertiesExtractor.getJobParameters(null, null);
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

        builder.addDate("date", Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        try {
            service.start("mainJob", builder.toJobParameters());
        } catch (JobInstanceAlreadyCompleteException e) {
            LOGGER.warn(
                    String.format(
                            "Job already completed for this date %s",
                            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ), e
            );
        }
    }



}
