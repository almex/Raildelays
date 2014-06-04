package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * Filter delay lower than a certain threshold.
 *
 * @author Almex
 */
public class FilterSmallDelaysProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FilterSmallDelaysProcessor.class);

    private Long threshold;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(threshold, "Threshold must be set");

        LOGGER.info("Processing for with threshold={}...");
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {
        BatchExcelRow result = null;

        if (item.getDelay() >= threshold) {
            result = item;
        }

        return result; // To apply the ItemReader contract
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

}
