package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;

/**
 * Filter items to get only two. One for departure and the other one for arrival.
 * The only remaining items are those which have the maximum delay for a given sens.
 *
 * @author Almex
 */
public class FilterTwoSensPerDayProcessor implements ItemProcessor<BatchExcelRow, BatchExcelRow>, InitializingBean {

    private ResourceAwareItemReaderItemStream<BatchExcelRow> outputReader;

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterTwoSensPerDayProcessor.class);

    private ExecutionContext executionContext;

    private String contextKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(outputReader, "outputReader is mandatory");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public BatchExcelRow process(final BatchExcelRow item) throws Exception {
        BatchExcelRow result = null;

        try {
            File resource = (File) executionContext.get(contextKey);

            if (resource != null) {
                outputReader.setResource(new FileSystemResource(resource));
            }

            outputReader.open(executionContext);

            try {
                BatchExcelRow matchingExcelRow = null;
                do {
                    matchingExcelRow = outputReader.read();

                    if (matchingExcelRow != null) {
                        if (new CompareToBuilder().append(item.getDate(), matchingExcelRow.getDate())
                                .append(item.getDepartureStation(), matchingExcelRow.getDepartureStation())
                                .append(item.getArrivalStation(), matchingExcelRow.getArrivalStation())
                                .toComparison() == 0) {
                            /**
                             * Here we know that we have a collision: we match the same date and the same sens.
                             * If the delay of the item is not greater than the one in the Excel sheet then we skip it.
                             */
                            if (item.getDelay() > matchingExcelRow.getDelay()) {
                                /**
                                 * Here, the delay of the item is greater than the matching Excel row.
                                 * We must replace the row currently in the Excel sheet with our item.
                                 */
                                result = item;
                                if (matchingExcelRow.getIndex() != null) {
                                    result.setIndex(matchingExcelRow.getIndex());
                                } else {
                                    throw new IllegalArgumentException("We don't know the current index of this Excel row. We cannot replace it!");
                                }
                            }

                            /**
                             * We stop searching here. Either the result is found or we have to skip this item.
                             */
                            break;
                        } else if (item.getDate().before(matchingExcelRow.getDate())) {
                            /**
                             * We stop searching. We expect that the content of the Excel file is sorted by date.
                             * This clause should never happen if the data read are also sorted by date.
                             */
                            break;
                        }
                    } else {
                        /**
                         * In that case we reach the first empty row without matching any previous data.
                         * So, we have to add a new row to the Excel sheet.
                         */
                        result = item;
                        result.setIndex(null);
                        break;
                    }
                } while (matchingExcelRow != null);
            } finally {
                outputReader.close();
            }
        } catch (ItemStreamException e) {
            LOGGER.warn("Error when opening ResourceAwareItemReaderItemStream. Maybe the resource is not available yet.", e);
            result = item;
        }

        return result;
    }

    public void setOutputReader(ResourceAwareItemReaderItemStream<BatchExcelRow> outputReader) {
        this.outputReader = outputReader;
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }
}
