package be.raildelays.batch.listener;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.railtime.TwoDirections;

/**
 * @author Almex
 */
public class LogStep1ItemProcessorListener extends AbstractLogItemProcessorListener<Object, Object> {
    @Override
    public void infoInput(String message, Object input) {
        if (input instanceof TwoDirections) {
            logger.info(message, (TwoDirections) input);
        }
    }

    @Override
    public void infoOutput(String message, Object output) {
        if (output instanceof LineStop) {
            LineStop current = (LineStop) output;

            while (current != null) {
                logger.info(message, current);
                current = current.getNext();
            }
        }
    }
}
