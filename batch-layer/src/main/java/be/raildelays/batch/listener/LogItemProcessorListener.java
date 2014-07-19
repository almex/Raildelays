package be.raildelays.batch.listener;

import be.raildelays.batch.processor.AggregateExpectedTimeProcessor;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;

/**
 * @author Almex
 */
public class LogItemProcessorListener {

    private static final Logger LOGGER = LoggerFactory.getLogger("xXx", AggregateExpectedTimeProcessor.class, '|');

    private static final String HEADER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";
    private static final String FOOTER = "|=====|====================|======|==========|====|====|============|============|=====|=====|=====|=====|==|======|======|";

    @BeforeProcess
    public void beforeProcess(Object item) {
        if (item instanceof LineStop) {
            LOGGER.info(HEADER);
            LOGGER.info("beforeProcess", (LineStop) item);
            LOGGER.info(FOOTER);
        }
    }

    @AfterProcess
    public void afterProcess(Object item, Object result) {
        if (item instanceof LineStop) {
            LOGGER.info(HEADER);
            if (result instanceof ExcelRow) {
                LOGGER.info("afterProcess", (ExcelRow) result);
            }
            LOGGER.info(FOOTER);
        }
    }
}
