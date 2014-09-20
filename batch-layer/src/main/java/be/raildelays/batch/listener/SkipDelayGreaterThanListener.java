 package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.processor.SkipDelayGreaterThanException;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * By default, we use <code>delayGreaterThan</code> as key name.
 *
 * @author Almex
 * @since 1.2
 * @see be.raildelays.batch.processor.SkipDelayGreaterThanProcessor
 * @see be.raildelays.batch.reader.LineStopInContextReader
 * @see be.raildelays.batch.processor.SkipDelayGreaterThanException
 */
public class SkipDelayGreaterThanListener extends StepListenerSupport<LineStop, BatchExcelRow> {

    private List<Long> skippedItemIds = new ArrayList<>();

    private String keyName = "delayGreaterThan";

    private static final Logger LOGGER = LoggerFactory.getLogger("Gtl", SkipDelayGreaterThanListener.class);

    @Override
    public void beforeStep(StepExecution stepExecution) {
        skippedItemIds.clear();

        LOGGER.trace("skip_count_reset", (LineStop) null);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus result = null; //-- By default we don't change the result

        if (!skippedItemIds.isEmpty()) {
            result = new ExitStatus("COMPLETED WITH DELAY GREATER THAN");

            //-- We store the collection of ids in the step execution context
            stepExecution.getExecutionContext().put(keyName, skippedItemIds);

            if (LOGGER.isTraceEnabled()) {
                for (Long id : skippedItemIds) {
                    LOGGER.trace("skipped_line_stop", new LineStop.Builder().id(id).build(false));
                }
            }
        }

        return result;
    }

    @Override
    public void onSkipInProcess(LineStop item, Throwable t) {
        if (t instanceof SkipDelayGreaterThanException && item != null) {
            skippedItemIds.add(item.getId());

            LOGGER.debug("skipping_item", item);
        }
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
