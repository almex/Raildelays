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
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Add step name and train id to {@link MDC} to be able to create
 * log direction based on that fields.
 *
 * @author Almex
 * @since 1.1
 */
public class LoggerContextStepListener implements StepExecutionListener {

    public static final String STEP_NAME = "stepName";
    public static final String TRAIN_ID = "trainId";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        MDC.put(STEP_NAME, stepExecution.getStepName().replace(':', '-'));
        if (stepExecution.getExecutionContext().containsKey(TRAIN_ID)) {
            MDC.put(TRAIN_ID, Integer.toString(stepExecution.getExecutionContext().getInt(TRAIN_ID)));
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        MDC.remove(STEP_NAME);
        MDC.remove(TRAIN_ID);
        return null;
    }
}
