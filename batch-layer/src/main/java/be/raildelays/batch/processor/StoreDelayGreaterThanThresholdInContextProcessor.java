package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Filter delay greater than a certain threshold and store the {@link be.raildelays.batch.bean.BatchExcelRow}
 * in the <code>Step</code> {@link org.springframework.batch.item.ExecutionContext}.
 * <p>
 * You should configure keyName name in order to match key used when retrieving this
 * {@link be.raildelays.batch.bean.BatchExcelRow} like below:
 * </p>
 * <p>
 * <code>
 * BatchExcelRow excelRow = (BatchExcelRow) context.get(keyName);
 * </code>
 * </p>
 *
 * @author Almex
 * @see be.raildelays.batch.reader.BatchExcelRowInContextReader
 * @since 1.2
 */
public class StoreDelayGreaterThanThresholdInContextProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fas", StoreDelayGreaterThanThresholdInContextProcessor.class);

    private Long threshold;

    private ExecutionContext context;

    private String keyName;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.context = stepExecution.getExecutionContext();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(threshold, "The 'threshold' property must be provided");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {

        if (item.getDelay() < threshold) {
            LOGGER.debug("keep_delay<" + threshold, item);
        } else {
            storeInContext(item);
        }

        return item;
    }

    private void storeInContext(final BatchExcelRow item) {
        /*
         * We can only have one train the same day having more than one hour of delay.
         * But we take the greatest one.
         */
        if (context.containsKey(keyName)) {
            BatchExcelRow itemInContext = (BatchExcelRow) context.get(keyName);

            if (itemInContext.getDelay() < item.getDelay()) {
                context.put(keyName, item);

                LOGGER.debug("replace_in_context", itemInContext);
            } else {
                LOGGER.debug("not_replace_in_context", itemInContext);
            }
        } else {
            context.put(keyName, item);

            LOGGER.debug("store_in_context", item);
        }
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
