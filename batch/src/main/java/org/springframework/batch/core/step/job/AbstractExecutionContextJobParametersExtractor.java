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

import java.util.Arrays;
import java.util.Date;

/**
 * Sub-classes of this abstract class should extract keys from an {@link ExecutionContext} and map them into an
 * equivalent {@link JobParameter}.
 *
 * @author Almex
 * @see StepExecutionContextJobParametersExtractor
 * @see JobExecutionContextJobParametersExtractor
 * @since 2.0
 */
public abstract class AbstractExecutionContextJobParametersExtractor implements JobParametersExtractor {

    private String[] keys = new String[0];
    private boolean useAllContextAttributes = true;

    protected JobParameters addJobParametersFromContext(ExecutionContext context) {
        JobParametersBuilder builder = new JobParametersBuilder();

        if (context != null) {
            if (useAllContextAttributes) {
                context.entrySet().stream().forEach(entry -> addParameter(builder, entry.getKey(), entry.getValue()));
            } else {
                Arrays.stream(keys).filter(context::containsKey).forEach(key -> addParameter(builder, key, context.get(key)));
            }
        }

        return builder.toJobParameters();
    }

    /**
     * A {@code null} value is interpreted as not adding the parameter.
     */
    private static void addParameter(JobParametersBuilder builder, String key, Object value) {
        JobParameter jobParameter = buildJobParameter(value);

        if (jobParameter != null) {
            builder.addParameter(key, jobParameter);
        }
    }

    /**
     * Build a {@link JobParameter} based on the type of the given {@code value}.
     *
     * @param value {@link Object} to translate into a {@link JobParameter}
     * @return If the value is not {@code null}, return a {@link JobParameter}, otherwise return {@code null}
     */
    private static JobParameter buildJobParameter(Object value) {
        JobParameter result = null;

        if (value instanceof Date) {
            result = new JobParameter((Date) value);
        } else if (value instanceof Long) {
            result = new JobParameter((Long) value);
        } else if (value instanceof Integer) {
            result = new JobParameter(((Integer) value).longValue());
        } else if (value instanceof Double) {
            result = new JobParameter((Double) value);
        } else if (value instanceof Float) {
            result = new JobParameter(((Float) value).doubleValue());
        } else if (value instanceof String) {
            result = new JobParameter((String) value);
        } else if (value != null) {
            result = new JobParameter(value.toString());
        }

        return result;
    }

    /**
     * @param keys list of key to extract from the {@link ExecutionContext} (only usable in combination of
     *             {@link #setUseAllContextAttributes(boolean)}) setted to {@code false}.
     */
    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * @param useAllContextAttributes {@code true} if we retrieve all context attributes or {@code false} if we keep
     *                                only some of them by defining {@link #setKeys(String[])}.
     *                                By default it's {@code true}
     */
    public void setUseAllContextAttributes(boolean useAllContextAttributes) {
        this.useAllContextAttributes = useAllContextAttributes;
    }
}
