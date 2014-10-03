package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

/**
 * Retrieve LineStop corresponding to the train id/date parameters.
 * This implementation can only be ran once.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.FilterByDelayMaxAndStoreInContextProcessor
 * @since 1.2
 */
public class BatchExcelRowInContextReader implements ItemStreamReader<BatchExcelRow> {

    private String keyName;

    private BatchExcelRow singleton;


    @Override
    public BatchExcelRow read() throws Exception {
        BatchExcelRow result = singleton;

        singleton = null; //-- We consume it then next time we return null to promote EOF

        return result;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        singleton = (BatchExcelRow) executionContext.remove(keyName);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
