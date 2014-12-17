package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Get the {@link be.raildelays.batch.bean.BatchExcelRow} retrieved from the <code>Step</code>
 * {@link org.springframework.batch.item.ExecutionContext}.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 * @since 1.2
 */
public class BatchExcelRowInContextReader implements ItemStreamReader<BatchExcelRow>, InitializingBean {

    private String keyName;

    private IteratorItemReader<BatchExcelRow> delegate;


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(keyName, "The 'keyName' property must be provided");
    }

    @Override
    public BatchExcelRow read() throws Exception {
        return delegate.read();
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(keyName)) {
            Map<Sens, BatchExcelRow> map = (Map) executionContext.get(keyName);

            delegate = new IteratorItemReader<BatchExcelRow>(map.values());
        } else {
            throw new IllegalStateException("Context should contain a value with this key : '" + keyName + "'");
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //NOOP
    }

    @Override
    public void close() throws ItemStreamException {
        //NOOP
    }
}
