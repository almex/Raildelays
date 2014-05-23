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
 * Filter items to get only two. One for departure and the other one for arrival.
 * The only remaining items are those which have the maximum delay for a given sens.
 *
 * @author Almex
 */
public class FilterSmallDelaysProcessor implements
        ItemProcessor<List<BatchExcelRow>, List<BatchExcelRow>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(FilterSmallDelaysProcessor.class);

    private Long threshold;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(threshold, "Threshold must be set");

        LOGGER.info("Processing for with threshold={}...");
    }

    @Override
    public List<BatchExcelRow> process(final List<BatchExcelRow> items) throws Exception {
        List<BatchExcelRow> result = new ArrayList<>();

        for (BatchExcelRow item : items) {
            if (item.getDelay() >= threshold) {
                result.add(item);
            }
        }

        return result.size() == 0 ? null : result; // To apply the ItemReader contract
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

}
