package be.raildelays.batch.listener;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.xls.ExcelRow;

/**
 * @author Almex
 */
public class LogStep2ItemProcessorListener extends AbstractLogItemProcessorListener<Object, Object> {
    @Override
    public void infoInput(String message, Object input) {
        if (input instanceof LineStop) {
            logger.info(message, (LineStop) input);
        }
    }

    @Override
    public void infoOutput(String message, Object output) {
        if (output instanceof ExcelRow) {
            logger.info(message, (ExcelRow) output);
        }
    }
}
