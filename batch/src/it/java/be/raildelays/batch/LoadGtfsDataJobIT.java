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

import be.raildelays.domain.Language;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = {"/jobs/steps/load-gtfs-into-database-job-context.xml"})
public class LoadGtfsDataJobIT extends AbstractContextIT {

    /**
     * SUT.
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void testLoadFromGtfs() {
        try {
            Map<String, JobParameter> parameters = new HashMap<>();

            parameters.put("date", new JobParameter(new SimpleDateFormat("yyyyMMdd").parse("20150101")));
            parameters.put("language", new JobParameter(Language.FR.name()));

            JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters(parameters));
            BatchStatus batchStatus = jobExecution.getStatus();

            Assert.assertFalse(batchStatus.isUnsuccessful());
            StepExecution stepExecution = jobExecution.getStepExecutions()
                    .stream()
                    .filter(step -> step.getStepName().equals("loadTrainStep"))
                    .findFirst()
                    .get();
            Assert.assertEquals(0, stepExecution.getSkipCount());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Your batch job has failed due to an exception.");
        }


    }
}
