package be.raildelays.batch.test;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Created by Almex on 18/12/2014.
 */
public class FailOnNegativeThresholdDateTasklet implements Tasklet {

    private Long thresholdDelay;

    public enum Status {COMPLETED, COMPLETED_WITH_60M_DELAY, FAILED}

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

         if (thresholdDelay < 0) {
                throw new Exception("Failed");
         }

        return RepeatStatus.FINISHED;
    }

    public void setThresholdDelay(Long thresholdDelay) {
        this.thresholdDelay = thresholdDelay;
    }
}
