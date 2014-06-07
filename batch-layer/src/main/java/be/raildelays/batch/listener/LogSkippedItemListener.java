package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.entities.LineStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;

/**
 * This class logs skipped item via Slf4j.
 *
 * @author Almex
 */
public class LogSkippedItemListener implements SkipListener<Object, BatchExcelRow>, ItemProcessListener<Object, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogSkippedItemListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        LOGGER.info("[READ] Skipped item {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(BatchExcelRow item, Throwable t) {
        LOGGER.info("[PROCESS] Skipped item {} - {}", item, t.getMessage());

    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        LOGGER.info("[WRITE] Skipped item {} - {}", item, t.getMessage());
    }

    @Override
    public void beforeProcess(Object item) {
    }

    @Override
    public void afterProcess(Object item, Object result) {
        if (item != null && result == null) {
            LOGGER.info("[PROCESS] Filtered item {}", item);
        }
    }

    @Override
    public void onProcessError(Object item, Exception e) {
    }
}
