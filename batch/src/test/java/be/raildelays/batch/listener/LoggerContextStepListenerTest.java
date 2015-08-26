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
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.test.MetaDataInstanceFactory;

@RunWith(BlockJUnit4ClassRunner.class)
public class LoggerContextStepListenerTest {

    public static final String STEP_NAME = "myStep";
    public static final Long EXECUTION_ID = 100L;
    public static final Integer TRAIN_ID = 10;
    private LoggerContextStepListener listener;
    private StepExecution stepExecution;

    @Before
    public void setUp() throws Exception {
        ExecutionContext context = new ExecutionContext();

        context.putInt(LoggerContextStepListener.TRAIN_ID, TRAIN_ID);
        listener = new LoggerContextStepListener();
        stepExecution = MetaDataInstanceFactory.createStepExecution(STEP_NAME, EXECUTION_ID);
        stepExecution.setExecutionContext(context);
    }

    @Test
    public void testBeforeJob() throws Exception {
        listener.beforeStep(stepExecution);

        Assert.assertEquals(TRAIN_ID.toString(), MDC.get(LoggerContextStepListener.TRAIN_ID));
        Assert.assertEquals(STEP_NAME, MDC.get(LoggerContextStepListener.STEP_NAME));
    }

    @Test
    public void testAfterJob() throws Exception {
        listener.afterStep(stepExecution);

        Assert.assertNull(MDC.get(LoggerContextStepListener.TRAIN_ID));
        Assert.assertNull(MDC.get(LoggerContextStepListener.STEP_NAME));
    }
}