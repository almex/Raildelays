package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

/**
 * Get the {@link be.raildelays.batch.bean.BatchExcelRow} retrieved from the <code>Step</code>
 * {@link org.springframework.batch.item.ExecutionContext}. This implementation can only read one item.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 * @since 1.2
 */
public class BatchExcelRowInContextReader implements ItemStreamReader<BatchExcelRow> {

    private String keyName;

    private BatchExcelRow singleton;


    @Override
    public BatchExcelRow read() throws Exception {
        BatchExcelRow result = singleton;

        singleton = null; // We consume it then next time we return null to promote EOF

        return result;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        singleton = (BatchExcelRow) executionContext.get(keyName);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}
