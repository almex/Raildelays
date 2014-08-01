package be.raildelays.batch.writer;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import java.util.List;

/**
 * Iterate over a list of a list of elements of type <code>T</code> and delegates writes to another writer.
 *
 * @param <T> type of the input items
 * @author Almex
 */
public class ListIterateItemWriter<T> implements ItemStreamWriter<List<? extends T>> {

    private ItemStreamWriter<T> delegate;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void write(List<? extends List<? extends T>> items) throws Exception {
        for (List<? extends T> item : items) {
            delegate.write(item);
        }
    }

    public void setDelegate(ItemStreamWriter<T> delegate) {
        this.delegate = delegate;
    }
}
