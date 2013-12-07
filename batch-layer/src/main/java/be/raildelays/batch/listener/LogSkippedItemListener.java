package be.raildelays.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.SkipListener;

/**
 * This class logs skipped item via Slf4j.
 *
 * @author Almex
 */
public class LogSkippedItemListener implements SkipListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogSkippedItemListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        LOGGER.info("[READ] Skipped item {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        LOGGER.info("[PROCESS] Skipped item {} - {}", item, t.getMessage());

    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        LOGGER.info("[WRITE] Skipped item {} - {}", item, t.getMessage());
    }
}
