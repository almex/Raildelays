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

package be.raildelays.batch.listener;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(BlockJUnit4ClassRunner.class)
public class LoggerContextJobListenerTest {

    public static final Date DATE = new Date();
    public static final String JOB_NAME = "myJob";
    public static final Long INSTANCE_ID = 1L;
    public static final Long EXECUTION_ID = 1L;
    private LoggerContextJobListener listener;
    private JobExecution jobExecution;

    @Before
    public void setUp() throws Exception {
        JobParametersBuilder builder = new JobParametersBuilder();

        builder.addParameter("date", new JobParameter(DATE));
        listener = new LoggerContextJobListener();
        jobExecution = MetaDataInstanceFactory.createJobExecution(JOB_NAME,
                INSTANCE_ID,
                EXECUTION_ID,
                builder.toJobParameters());
    }

    @Test
    public void testBeforeJob() throws Exception {
        listener.beforeJob(jobExecution);

        Assert.assertEquals(EXECUTION_ID.toString(), MDC.get(LoggerContextJobListener.JOB_EXECUTION_ID));
        Assert.assertEquals(INSTANCE_ID.toString(), MDC.get(LoggerContextJobListener.JOB_INSTANCE_ID));
        Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(DATE), MDC.get(LoggerContextJobListener.DATE_PARAMETER));
    }

    @Test
    public void testAfterJob() throws Exception {
        listener.afterJob(jobExecution);

        Assert.assertNull(MDC.get(LoggerContextJobListener.JOB_EXECUTION_ID));
        Assert.assertNull(MDC.get(LoggerContextJobListener.JOB_INSTANCE_ID));
        Assert.assertNull(MDC.get(LoggerContextJobListener.DATE_PARAMETER));
    }
}