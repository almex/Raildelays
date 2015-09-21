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

package org.springframework.batch.core.step.job;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.item.ExecutionContext;

import java.util.Date;
import java.util.Map;

/**
 * Sub-classes of this abstract class should extract keys from an {@link ExecutionContext} and map them into an
 * equivalent {@link JobParameter}.
 *
 * @see StepExecutionContextJobParametersExtractor
 * @see JobExecutionContextJobParametersExtractor
 * @author Almex
 * @since 2.0
 */
public abstract class AbstractExecutionContextJobParametersExtractor implements JobParametersExtractor {

    protected static JobParameters addJobParametersFromContext(JobParameters jobParameters, ExecutionContext context) {
        JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

        for (Map.Entry<String, Object> entry : context.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Date) {
                builder.addDate(entry.getKey(), (Date) value);
            } else if (value instanceof Long) {
                builder.addLong(entry.getKey(), (Long) value);
            } else if (value instanceof Integer) {
                builder.addLong(entry.getKey(), ((Integer) value).longValue());
            } else if (value instanceof Double) {
                builder.addDouble(entry.getKey(), (Double) value);
            } else if (value instanceof Float) {
                builder.addDouble(entry.getKey(), ((Float) value).doubleValue());
            } else if (value instanceof String) {
                builder.addString(entry.getKey(), (String) value);
            } else if (value != null) {
                builder.addString(entry.getKey(), value.toString());
            } else {
                builder.addString(entry.getKey(), null);
            }
        }

        return builder.toJobParameters();
    }
}
