package be.raildelays.batch.processor;

import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Filter delay greater than a certain threshold or filter delay less or equal to a certain depending on the
 * {@link Mode}.
 *
 * @author Almex
 * @since 1.2
 */
public class FilterWithThresholdDelayProcessor implements ItemProcessor<ExcelRow, ExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fas", FilterWithThresholdDelayProcessor.class);

    private Long threshold;
    private Mode mode;


    public enum Mode {
        FILTER_LESS_THAN {
            @Override
            boolean filter(ExcelRow item, Long threshold) {
                return item.getDelay() < threshold;
            }
        },
        FILTER_GREATER_OR_EQUAL_TO {
            @Override
            boolean filter(ExcelRow item, Long threshold) {
                return item.getDelay() >= threshold;
            }
        };

        abstract boolean filter(ExcelRow item, Long threshold);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(threshold, "The 'threshold' property must be provided");
    }

    @Override
    public ExcelRow process(final ExcelRow item) throws Exception {
        ExcelRow result;

        if (mode.filter(item, threshold)) {
            result = null;
            LOGGER.debug("filter:" + threshold, item);
        } else {
            result = item;
            LOGGER.debug("keep:" + threshold, item);
        }

        return result;
    }

    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
}
