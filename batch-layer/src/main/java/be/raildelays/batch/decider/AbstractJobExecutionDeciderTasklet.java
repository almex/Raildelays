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
 */.raildelays.batch.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContextRepeatCallback;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;

/**
 * Mix between a {@link Tasklet} and a {@link JobExecutionDecider}.
 * You can choose to use it as one or the other.
 * <p>
 * Note : A {@link JobExecutionDecider} can be more readable but only a {@link Tasklet} can have access to
 * {@code scope="step"}.
 */
public abstract class AbstractJobExecutionDeciderTasklet implements JobExecutionDecider, Tasklet {

    @Override
    public FlowExecutionStatus decide(final JobExecution jobExecution, final StepExecution stepExecution) {
        StepContribution contribution = stepExecution.createStepContribution();

        new RepeatTemplate().iterate(
                new StepContextRepeatCallback(stepExecution) {
                    @Override
                    public RepeatStatus doInChunkContext(RepeatContext context, ChunkContext chunkContext) throws Exception {
                        return execute(contribution, chunkContext);
                    }
                });

        stepExecution.apply(contribution);

        return new FlowExecutionStatus(stepExecution.getExitStatus().getExitCode());
    }

    @Override
    public abstract RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception;
}
