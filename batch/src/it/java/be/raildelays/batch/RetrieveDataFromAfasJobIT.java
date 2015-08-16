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

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ContextConfiguration(locations = {
        "/jobs/steps/retrieve-data-from-afas-job-context.xml"})
public class RetrieveDataFromAfasJobIT extends AbstractContextIT {

    /**
     * SUT.
     */
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    @Ignore
    public void testGrabLineStop() {
        BatchStatus batchStatus;

        try {
            Map<String, JobParameter> parameters = new HashMap<>();
            Calendar today = Calendar.getInstance();
            Date date;

            if (today.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                    today.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                date = DateUtils.addDays(today.getTime(), -2);
            } else {
                date = today.getTime();
            }

            parameters.put("input.file.path", new JobParameter("train-list.properties"));
            parameters.put("date", new JobParameter(date));
            parameters.put("station.a.name", new JobParameter("Liège-Guillemins"));
            parameters.put("station.b.name", new JobParameter("Brussels (Bruxelles)-Central"));
            parameters.put("output.file.path", new JobParameter("file:./target/output.dat"));

            batchStatus = jobLauncherTestUtils.launchJob(new JobParameters(parameters)).getStatus();

            Assert.assertFalse(batchStatus.isUnsuccessful());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Your batch job has failed due to an exception.");
        }


    }
}
