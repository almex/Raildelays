package be.raildelays.batch.decider;

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
