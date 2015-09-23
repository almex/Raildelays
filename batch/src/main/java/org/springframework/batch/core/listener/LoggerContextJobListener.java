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

package org.springframework.batch.core.listener;

import org.slf4j.MDC;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Add jobExecutionId to {@link MDC} to be able to create
 * log directory based on that number.
 *
 * @author Almex
 * @since 1.1
 */
public class LoggerContextJobListener implements JobExecutionListener {

    public static final String JOB_EXECUTION_ID = "jobExecutionId";
    public static final String JOB_INSTANCE_ID = "jobInstanceId";
    public static final String DATE_PARAMETER = "dateParameter";

    @Override
    public void beforeJob(JobExecution jobExecution) {
        Date date = jobExecution.getJobParameters().getDate("date");

        MDC.put(JOB_EXECUTION_ID, jobExecution.getId().toString());
        MDC.put(JOB_INSTANCE_ID, jobExecution.getJobId().toString());
        MDC.put(DATE_PARAMETER, new SimpleDateFormat("yyyy-MM-dd").format(date));
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        MDC.remove(JOB_EXECUTION_ID);
        MDC.remove(JOB_INSTANCE_ID);
        MDC.remove(DATE_PARAMETER);
    }
}
