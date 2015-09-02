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

package be.raildelays.batch.decider;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.batch.item.support.ItemStreamItemReaderDelegator;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.support.ResourceAwareItemStream;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.time.LocalDate;
import java.util.Collections;

/**
 * @author Almex
 */
public class MoreThanOneHourDelayDeciderTest {

    public static final FlowExecutionStatus COMPLETED_WITH_60_M_DELAY =
            new FlowExecutionStatus(MoreThanOneHourDelayDecider.COMPLETED_WITH_60_M_DELAY.getExitCode());
    private MoreThanOneHourDelayDecider decider;

    @Before
    public void setUp() throws Exception {
        decider = new MoreThanOneHourDelayDecider();
        decider.setKeyName("foo");
        decider.setThresholdDelay(15L);
        decider.setReader(new SimpleReader());
        decider.afterPropertiesSet();
    }

    /**
     * We expect to be over the threshold and that the reader match the requirements in order to get
     * COMPLETED_WITH_60_M_DELAY.
     */
    @Test
    public void testCOMPLETED_WITH_60_M_DELAY() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(COMPLETED_WITH_60_M_DELAY, status);
    }

    /**
     * We expect to be over the threshold and that the reader does not match the requirements in order to get
     * FAILED.
     */
    @Test
    public void testFAILED() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        decider.setReader(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Collections.singletonList(
                new ExcelRow
                        .Builder(LocalDate.now(), Sens.ARRIVAL)
                        .delay(65L)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.FAILED, status);
    }

    /**
     * We expect to be under the threshold in order to get COMPLETED.
     */
    @Test
    public void testCOMPLETED() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();
        StepExecution stepExecution = jobExecution.createStepExecution("step1");

        decider.setReader(new ItemStreamItemReaderDelegator<>(new ListItemReader<>(Collections.singletonList(
                new ExcelRow
                        .Builder(LocalDate.now(), Sens.ARRIVAL)
                        .delay(0L)
                        .build(false)
        ))));

        FlowExecutionStatus status = decider.decide(jobExecution, stepExecution);

        Assert.assertEquals(FlowExecutionStatus.COMPLETED, status);
    }

    private class SimpleReader extends AbstractItemStreamItemReader<ExcelRow> implements ResourceAwareItemStream {

        private boolean done = false;

        @Override
        public ExcelRow read() throws Exception {
            ExcelRow result = null;

            if (!done) {
                result = new ExcelRow
                        .Builder(LocalDate.now(), Sens.ARRIVAL)
                        .delay(65L)
                        .build(false);
                done = true;
            }

            return result;
        }

        @Override
        public Resource getResource() {
            return new ClassPathResource("./");
        }

        @Override
        public void setResource(Resource resource) {

        }
    }
}