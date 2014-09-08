package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.xls.ExcelRow;
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

    private static final Logger READ = LoggerFactory.getLogger("RD", LogSkippedItemListener.class);

    private static final Logger PROCESS = LoggerFactory.getLogger("PR", LogSkippedItemListener.class);

    private static final Logger WRITE = LoggerFactory.getLogger("WR", LogSkippedItemListener.class);


    @Override
    public void onSkipInRead(Throwable t) {
        READ.debug(t.getMessage(), (ExcelRow) null);
    }

    @Override
    public void onSkipInWrite(BatchExcelRow item, Throwable t) {
        WRITE.info("on_skip_write", item);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        if (item instanceof LineStop) {
            PROCESS.info(t.getMessage(), (LineStop) item);
        } else if (item instanceof ExcelRow) {
            PROCESS.info(t.getMessage(), (ExcelRow) item);
        } else {
            PROCESS.info("unknown_type", (ExcelRow) null);
            PROCESS.debug(t.getMessage(), (ExcelRow) null);
        }
    }

    @Override
    public void beforeProcess(Object item) {
    }

    @Override
    public void afterProcess(Object item, Object result) {
        if (item != null && result == null) {
            if (item instanceof LineStop) {
                PROCESS.info("filtering", (LineStop) item);
            } else if (item instanceof ExcelRow) {
                PROCESS.info("filtering", (ExcelRow) item);
            } else {
                PROCESS.info("filtering_unknown", (ExcelRow) null);
            }
        }
    }

    @Override
    public void onProcessError(Object item, Exception e) {
    }
}
