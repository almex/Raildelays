package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.processor.AggregateExpectedTimeProcessor;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;

/**
 * This class logs skipped item via Slf4j.
 *
 * @author Almex
 */
public class LogSkippedItemListener implements SkipListener<Object, BatchExcelRow>, ItemProcessListener<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger("Ftr", LogSkippedItemListener.class);


    @Override
    public void onSkipInRead(Throwable t) {
        LOGGER.info("[READ] Skipped item {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(BatchExcelRow item, Throwable t) {
        LOGGER.info("on_skip_write", item);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        LOGGER.info("on_skip_process", item);
    }

    @Override
    public void beforeProcess(Object item) {
    }

    @Override
    public void afterProcess(Object item, Object result) {
        if (item != null && result == null) {
            LOGGER.info("filtering", item);
        }
    }

    @Override
    public void onProcessError(Object item, Exception e) {
    }
}
