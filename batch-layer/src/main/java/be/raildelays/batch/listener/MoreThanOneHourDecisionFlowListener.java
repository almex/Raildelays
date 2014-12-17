package be.raildelays.batch.listener;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * This listener is responsible to change the {@link org.springframework.batch.core.ExitStatus} of the <code>Step</code>
 * when we get an item with a delay greater than the max threshold in the
 * {@link org.springframework.batch.item.ExecutionContext}. Then we return <code>COMPLETED_WITH_60M_DELAY</code> in
 * order to go to extra steps to handle this particular item.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 * @deprecated use {@link be.raildelays.batch.decider.DelayMoreThanOneHourDecider} instead
 */
public class MoreThanOneHourDecisionFlowListener extends StepExecutionListenerSupport implements InitializingBean {

    private String keyName;

    private ExecutionContext executionContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus finalStatus = null;

        if (stepExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            if (executionContext.containsKey(keyName)) {
                finalStatus = new ExitStatus("COMPLETED_WITH_60M_DELAY");
            }
        }

        return finalStatus;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
