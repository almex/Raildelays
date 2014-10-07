package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.apache.commons.lang.Validate;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

/**
 * Filter delay greater than a certain threshold and store the trainId in the job {@link ExecutionContext}.
 * <p>
 * You should configure keyName name in order to match key used when retrieving this <code>trainId</code> like below:
 * </p>
 * <p>
 * <code>
 * Long trainId = context.getLong(keyName);
 * </code>
 * </p>
 *
 * @author Almex
 * @see be.raildelays.batch.reader.ByTrainIdAndDateLineStopReader
 * @since 1.2
 */
public class FilterByDelayThresholdAndStoreTrainIdProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fas", FilterByDelayThresholdAndStoreTrainIdProcessor.class);

    private Long threshold;

    private ExecutionContext context;

    private String keyName;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.context = stepExecution.getExecutionContext();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(threshold, "Threshold must be set");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {
        BatchExcelRow result = null;

        LOGGER.trace("item", item);

        if (item.getDelay() < threshold) {
            result = item;

            LOGGER.debug("keep_delay<" + threshold, item);
        } else {
            storeInContext(item);

            LOGGER.debug("store_in_context", item);
        }


        LOGGER.trace("result", result);

        return result; // To apply the ItemReader contract
    }

    private void storeInContext(final BatchExcelRow item) {
        if (item.getExpectedTrain1() != null) {
            /*
             * We can only have one train the same day having more than one hour of delay.
             */
            context.putLong(keyName, item.getExpectedTrain1().getId());
        }
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
