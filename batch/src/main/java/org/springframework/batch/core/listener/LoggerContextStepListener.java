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
import org.springframework.batch.core.*;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Add step name and trainLine id to {@link org.apache.logging.log4j.ThreadContext} to be able to create
 * log direction based on that fields.
 *
 * @author Almex
 * @since 1.1
 */
public class LoggerContextStepListener implements StepExecutionListener, InitializingBean {

    public static final String STEP_NAME = "stepName";
    public static final String STEP_EXECUTION_ID = "stepExecutionId";
    private JobParametersExtractor jobParametersExtractor;
    private Set<String> keys = new HashSet<>(); // To keep trace of what we have to clean-up at the end
    private String dateFormat = "yyyy-MM-dd";

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(jobParametersExtractor, "The property 'jobParametersExtractor' is mandatory");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        MDC.put(STEP_NAME, stepExecution.getStepName().replace(':', '-'));
        MDC.put(STEP_EXECUTION_ID, stepExecution.getId().toString());
        addFromJobParametersExtractor(stepExecution);
    }

    private void addFromJobParametersExtractor(StepExecution stepExecution) {
        JobParameters jobParameters = jobParametersExtractor.getJobParameters(null, stepExecution);

        for (String key : jobParameters.getParameters().keySet()) {
            JobParameter jobParameter = jobParameters.getParameters().get(key);
            String value;

            switch (jobParameter.getType()) {
                case DATE:
                    Date date = (Date) jobParameter.getValue();

                    value = new SimpleDateFormat(dateFormat).format(date);
                    break;
                default:
                    value = jobParameter.getValue().toString();
            }

            MDC.put(key, value);
            keys.add(key);
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        MDC.remove(STEP_NAME);
        MDC.remove(STEP_EXECUTION_ID);
        keys.forEach(MDC::remove);
        return null;
    }

    public void setJobParametersExtractor(JobParametersExtractor jobParametersExtractor) {
        this.jobParametersExtractor = jobParametersExtractor;
    }

    /**
     * @param dateFormat used by a {@link SimpleDateFormat} to format {@link JobParameter.ParameterType#DATE} type.
     *                   By default, we use 'yyyy-MM-dd' format.
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
