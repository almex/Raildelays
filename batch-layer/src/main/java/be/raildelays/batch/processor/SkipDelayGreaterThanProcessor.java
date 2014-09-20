package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.apache.commons.lang.Validate;
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
 * @see be.raildelays.batch.reader.LineStopInContextReader
 * @see be.raildelays.batch.listener.SkipDelayGreaterThanListener
 * @since 1.2
 */
public class SkipDelayGreaterThanProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fas", SkipDelayGreaterThanProcessor.class);

    private Long threshold;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(threshold, "Threshold must be set");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws SkipDelayGreaterThanException {
        BatchExcelRow result;

        LOGGER.trace("item", item);

        if (item.getDelay() < threshold) {
            result = item;

            LOGGER.debug("keep_delay<" + threshold, item);
        } else {
            LOGGER.debug("keep_delay>=" + threshold, item);

            throw new SkipDelayGreaterThanException(item, threshold);
        }

        LOGGER.trace("result", result);

        return result; // To apply the ItemReader contract
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }
}
