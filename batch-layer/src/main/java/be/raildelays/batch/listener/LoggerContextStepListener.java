package be.raildelays.batch.listener;

import org.slf4j.MDC;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * Add step name and train id to {@link org.apache.logging.log4j.ThreadContext} to be able to create
 * log direction based on that fields.
 *
 * @author Almex
 */
public class LoggerContextStepListener implements StepExecutionListener {

    public static final String STEP_NAME = "stepName";
    public static final String TRAIN_ID = "trainId";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        MDC.put(STEP_NAME, stepExecution.getStepName().replace(':', '-'));
        if (stepExecution.getExecutionContext().containsKey(TRAIN_ID)) {
            MDC.put(TRAIN_ID, Integer.toString(stepExecution.getExecutionContext().getInt(TRAIN_ID)));
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        MDC.remove(STEP_NAME);
        MDC.remove(TRAIN_ID);
        return null;
    }
}
