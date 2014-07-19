package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.apache.commons.lang.Validate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

/**
 * Filter delay lower than a certain threshold.
 *
 * @author Almex
 */
public class FilterSmallDelaysProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Thr", FilterSmallDelaysProcessor.class);

    private Long threshold;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(threshold, "Threshold must be set");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {
        BatchExcelRow result = null;

        LOGGER.trace("item", item);

        if (item.getDelay() >= threshold) {
            LOGGER.debug("keep_delay>=" + threshold, item);
            result = item;
        }

        LOGGER.trace("result", result);

        return result; // To apply the ItemReader contract
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

}
