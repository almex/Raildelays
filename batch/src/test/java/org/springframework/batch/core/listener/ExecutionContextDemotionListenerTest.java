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

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.util.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class ExecutionContextDemotionListenerTest {

    private static final String key = "testKey";
    private static final String value = "testValue";
    private static final String key2 = "testKey2";
    private static final String value2 = "testValue2";
    private ExecutionContextDemotionListener listener;

    @Before
    public void setUp() {
        listener = new ExecutionContextDemotionListener();
    }

    /**
     * CONDITION: ExecutionContext contains {key, key2}. keys = {key}. statuses
     * is not set (defaults to {COMPLETED}).
     * <p>
     * EXPECTED: key is promoted. key2 is not.
     */
    @Test
    public void demoteEntryNullStatuses() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = jobExecution.createStepExecution("step1");
        stepExecution.setExitStatus(ExitStatus.COMPLETED);

        Assert.state(jobExecution.getExecutionContext().isEmpty());
        Assert.state(stepExecution.getExecutionContext().isEmpty());

        jobExecution.getExecutionContext().putString(key, value);
        jobExecution.getExecutionContext().putString(key2, value2);

        listener.setKeys(new String[]{key});
        listener.afterPropertiesSet();
        listener.beforeStep(stepExecution);

        assertEquals(value, stepExecution.getExecutionContext().getString(key));
        assertFalse(stepExecution.getExecutionContext().containsKey(key2));
    }

    /**
     * CONDITION: keys = {key, key2}. Only {key} exists in the ExecutionContext.
     * <p>
     * EXPECTED: key is promoted. key2 is not.
     */
    @Test
    public void promoteEntriesKeyNotFound() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        Assert.state(jobExecution.getExecutionContext().isEmpty());
        Assert.state(stepExecution.getExecutionContext().isEmpty());

        jobExecution.getExecutionContext().putString(key, value);

        listener.setKeys(new String[]{key, key2});
        listener.afterPropertiesSet();
        listener.beforeStep(stepExecution);

        assertEquals(value, stepExecution.getExecutionContext().getString(key));
        assertFalse(stepExecution.getExecutionContext().containsKey(key2));
    }

    /**
     * CONDITION: keys = {key}. key is already in job but not in step.
     * <p>
     * EXPECTED: key is not erased.
     */
    @Test
    public void demoteEntriesKeyNotFoundInStep() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        Assert.state(jobExecution.getExecutionContext().isEmpty());
        Assert.state(stepExecution.getExecutionContext().isEmpty());

        jobExecution.getExecutionContext().putString(key, value);

        listener.setKeys(new String[]{key});
        listener.afterPropertiesSet();
        listener.beforeStep(stepExecution);

        assertEquals(value, stepExecution.getExecutionContext().getString(key));
    }

    /**
     * CONDITION: strict = true. keys = {key, key2}. Only {key} exists in the
     * ExecutionContext.
     * <p>
     * EXPECTED: IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void demoteEntriesKeyNotFoundStrict() throws Exception {
        JobExecution jobExecution = new JobExecution(1L);
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        Assert.state(jobExecution.getExecutionContext().isEmpty());
        Assert.state(stepExecution.getExecutionContext().isEmpty());

        jobExecution.getExecutionContext().putString(key, value);

        listener.setStrict(true);
        listener.setKeys(new String[]{key, key2});
        listener.afterPropertiesSet();
        listener.beforeStep(stepExecution);

        assertEquals(value, stepExecution.getExecutionContext().getString(key));
        assertFalse(stepExecution.getExecutionContext().containsKey(key2));
    }

    /**
     * CONDITION: keys = NULL
     * <p>
     * EXPECTED: IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void keysMustBeSet() throws Exception {
        // didn't set the keys, same as listener.setKeys(null);
        listener.afterPropertiesSet();
    }
}