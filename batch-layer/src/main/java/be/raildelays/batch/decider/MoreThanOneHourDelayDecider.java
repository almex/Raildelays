package be.raildelays.batch.decider;

import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.support.ResourceAwareItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.nio.file.Paths;

/**
 * This {@link JobExecutionDecider} is responsible to return a custom {@link FlowExecutionStatus}
 * when we get an item with a delay greater than the max threshold in the {@link ExecutionContext}.
 * Then we return <code>COMPLETED_WITH_60M_DELAY</code> in order to go to extra steps to handle this particular item.
 *
 * @since 1.2
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
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

    @Override
    protected ExitStatus doRead(StepContribution contribution, ExecutionContext context, ExcelRow item) throws Exception {
        ExitStatus result = ExitStatus.EXECUTING;

        if (item.getDelay() >= thresholdDelay) {
            if (reader instanceof ResourceAwareItemStream) {
                // We store the file path in the context
                context.put(keyName, Paths.get(((ResourceAwareItemStream) reader).getResource().getURI()));

                // We keep trace that we've stored something
                contribution.incrementWriteCount(1);
                result = COMPLETED_WITH_60_M_DELAY;
            }
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
