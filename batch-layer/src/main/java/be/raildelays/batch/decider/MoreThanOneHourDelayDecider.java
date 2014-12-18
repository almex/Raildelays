package be.raildelays.batch.decider;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * This {@link JobExecutionDecider} is responsible to return a custom {@link FlowExecutionStatus}
 * when we get an item with a delay greater than the max threshold in the {@link ExecutionContext}.
 * Then we return <code>COMPLETED_WITH_60M_DELAY</code> in order to go to extra steps to handle this particular item.
 *
 * @since 1.2
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 */
public class MoreThanOneHourDelayDecider implements JobExecutionDecider, InitializingBean {

    private String keyName;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.keyName, "The 'keyName' property must be provided");
    }

    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        // The default status will be COMPLETED (we arbitrary kept the original keyword to mark this step as succeed)
        FlowExecutionStatus finalStatus = FlowExecutionStatus.COMPLETED;
        ExecutionContext executionContext = stepExecution.getExecutionContext();

        /*
         * The Spring Batch documentation is not clear but from what I can understand :
         * if the previous step is COMPLETED then the stepExecution contains already that status
         * if the previous step FAILED then the the stepExecution have respectively STARTED/UNKNOWN as
         * BatchStatus/ExitStatus combination.
         * So I presume that no matter what the JobExectionDecider return, if an error occurs (an uncatched Exception)
         * then the step will FAILED. But the JobExectionDecider can return a custom FlowExecutionStatus
         * which will be interpreted as a failure by the flow.
         * ex:
         * <batch:decision id="decision" decider="myDecider">
         *      <batch:next on="FILE_NOT_FOUND" to="createFile"/>
         *      <batch:fail on="FILE_CORRUPTED"/>
         *      <batch:end on="FILE_ALREADY_EXISTS"/>
         * </batch:decision>
         *
         * So I presume that no matter what I return here if the step FAILED it will not be overrided.
         */

        // Only if the context contains what we expect we return our specific status
        if (executionContext.containsKey(keyName)) {
            finalStatus = new FlowExecutionStatus("COMPLETED_WITH_60M_DELAY");
        }

        return finalStatus;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
