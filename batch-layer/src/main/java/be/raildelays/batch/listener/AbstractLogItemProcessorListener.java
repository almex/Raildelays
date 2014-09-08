package be.raildelays.batch.listener;

import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;

/**
 * @author Almex
 */
public abstract class AbstractLogItemProcessorListener<I, O> {

    private static final String HEADER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";
    private static final String FOOTER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";
    protected Logger logger;

    public AbstractLogItemProcessorListener() {
        logger = LoggerFactory.getLogger("xXx", this.getClass(), '|');
    }

    @BeforeProcess
    public void beforeProcess(I item) {
        logger.info(HEADER);
        infoInput("beforeProcess", item);
        logger.info(FOOTER);
    }

    @AfterProcess
    public void afterProcess(I item, O result) {
        logger.info(HEADER);
        infoOutput("afterProcess", result);
        logger.info(FOOTER);
    }

    public abstract void infoInput(String message, I input);

    public abstract void infoOutput(String message, O output);
}
