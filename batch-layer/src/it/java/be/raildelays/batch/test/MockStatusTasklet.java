package be.raildelays.batch.test;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Map;

/**
 * Created by Almex on 18/12/2014.
 */
public class MockStatusTasklet implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
        ExecutionContext context = chunkContext.getStepContext().getStepExecution().getExecutionContext();

        Status status = Status.valueOf((String) jobParameters.get("status"));

        switch (status) {
            case COMPLETED:
                break;
            case COMPLETED_WITH_60M_DELAY:
                context.put("foo", "bar");
                break;
            case FAILED:
                throw new Exception("Failed");
        }

        return RepeatStatus.FINISHED;
    }

    public static enum Status {COMPLETED, COMPLETED_WITH_60M_DELAY, FAILED;}
}
