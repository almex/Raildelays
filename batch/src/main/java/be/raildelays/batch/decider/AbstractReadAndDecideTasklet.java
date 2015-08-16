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

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Almex
 * @since 1.2
 */
public abstract class AbstractReadAndDecideTasklet<T> extends AbstractJobExecutionDeciderTasklet implements InitializingBean {
    protected ItemStreamReader<T> reader;
    protected boolean opened;

    @Override
    public void afterPropertiesSet() throws Exception {
        opened = false;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        ExecutionContext context = chunkContext.getStepContext().getStepExecution().getExecutionContext();
        RepeatStatus result;

        if (!opened) {
            reader.open(context);
            opened = true;
        }

        try {
            T item = reader.read();

            reader.update(context);

            if (item != null) {
                // We keep trace of non-null read
                contribution.incrementReadCount();

                result = finished(contribution, doRead(contribution, context, item));
            } else {
                result = finished(contribution, ExitStatus.COMPLETED);
            }
        } catch (Exception e) {
            result = finished(contribution, ExitStatus.FAILED.addExitDescription(e));
        }

        reader.update(context);

        return result;
    }

    /**
     * Execute a logic upon the read of an item.
     *
     * @param contribution mutable state to be passed back to update the current step execution. Don't modify the
     *                     {@link ExitStatus} it will be override by the returned value.
     * @param context      ExecutionContext shared between each step components
     * @param item         a non-null read item
     * @return an {@link ExitStatus#isRunning()} if you want to continue to read and other {@link ExitStatus} if not
     * @throws Exception on any case of failure
     */
    protected abstract ExitStatus doRead(StepContribution contribution, ExecutionContext context, T item) throws Exception;

    /**
     * Decide either or not this task is finished.
     * If the task {@link ExitStatus#isRunning()} then it will continue otherwise it's finished.
     *
     * @param contribution mutable state to be passed back to update the current step execution
     * @param status       the {@link ExitStatus} which contributes in the step execution
     * @return {@link RepeatStatus#CONTINUABLE} there is more to read or {@link RepeatStatus#FINISHED} if not
     */
    private RepeatStatus finished(StepContribution contribution, ExitStatus status) {
        RepeatStatus result = RepeatStatus.CONTINUABLE;

        contribution.setExitStatus(status);

        if (!status.isRunning()) {
            reader.close();
            opened = false;

            result = RepeatStatus.FINISHED;
        }

        return result;
    }

    public void setReader(ItemStreamReader<T> reader) {
        this.reader = reader;
    }
}
