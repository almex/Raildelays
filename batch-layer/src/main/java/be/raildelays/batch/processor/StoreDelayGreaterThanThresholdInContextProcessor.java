package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Filter delay greater than a certain threshold and store the {@link be.raildelays.batch.bean.BatchExcelRow}
 * in the <code>Step</code> {@link org.springframework.batch.item.ExecutionContext}.
 * <p>
 * You should configure keyName name in order to match key used when retrieving this
 * {@link be.raildelays.batch.bean.BatchExcelRow} like below:
 * </p>
 * <p>
 * <code>
 * Map<Sens, BatchExcelRow> excelRow = (Map) context.get(keyName);
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
        Assert.notNull(keyName, "The 'keyName' property must be provided");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {

        if (item.getDelay() < threshold) {
            LOGGER.debug("delay<" + threshold, item);
        } else {
            storeInContext(item);
            LOGGER.debug("delay>=" + threshold, item);
        }

        return item;
    }

    private void storeInContext(final BatchExcelRow item) {
        Map<Sens, BatchExcelRow> list = null;

        /*
         * We can only have two trains  the same day having more than one hour of delay (one per sens).
         */
        if (context.containsKey(keyName)) {
            list = (Map) context.get(keyName);
        } else {
            list = new HashMap<>(2);
            context.put(keyName, list);
        }

        list.put(item.getSens(), item);

        LOGGER.debug("store_in_context", item);
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
