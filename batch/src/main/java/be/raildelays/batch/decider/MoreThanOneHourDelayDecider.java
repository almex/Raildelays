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

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.support.ResourceContextAccessibleItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.nio.file.Paths;

/**
 * This {@link JobExecutionDecider} is responsible to return a custom {@link FlowExecutionStatus}
 * when we get an item with a delay greater than the max threshold in the {@link ExecutionContext}.
 * Then we return <code>COMPLETED_WITH_60M_DELAY</code> in order to go to extra steps to handle this particular item.
 * The reader must be an instance of {@link ResourceContextAccessibleItemStream}.
 *
 * @since 1.2
 * @author Almex
 */
public class MoreThanOneHourDelayDecider extends AbstractReadAndDecideTasklet<ExcelRow> implements InitializingBean {

    public static final ExitStatus COMPLETED_WITH_60_M_DELAY = new ExitStatus("COMPLETED_WITH_60M_DELAY");
    private String keyName;
    private Long thresholdDelay;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
        Assert.notNull(this.thresholdDelay, "The 'thresholdDelay' property must be provided");
    }

    /**
     * {@inheritDoc}
     *
     * @throws UnexpectedInputException if the reader is neither an instance of {@link ResourceContextAccessibleItemStream} nor an
     *                                  instance of {@link MultiResourceItemReader}.
     */
    @Override
    protected ExitStatus doRead(StepContribution contribution, ExecutionContext context, ExcelRow item) throws Exception {
        ExitStatus result = ExitStatus.EXECUTING;

        if (item.getDelay() >= thresholdDelay) {
            Resource resource;

            if (reader instanceof ResourceContextAccessibleItemStream) {
                resource = ((ResourceContextAccessibleItemStream) reader).getResourceContext().getResource();
            } else {
                throw new UnexpectedInputException("The 'reader' should be an instance of ResourceContextAccessibleItemStream");
            }

            // We store the file path in the context
            context.put(keyName, Paths.get(resource.getURI()).toAbsolutePath().toString());

            // We keep trace that we've stored something
            contribution.incrementWriteCount(1);
            result = COMPLETED_WITH_60_M_DELAY;
        }

        return result;
    }


    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setThresholdDelay(Long thresholdDelay) {
        this.thresholdDelay = thresholdDelay;
    }
}
