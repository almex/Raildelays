package be.raildelays.batch.writer;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Iterate over a list of a list of elements of type <code>T</code> and delegates writes to another writer.
 *
 * @param <T> type of the input items
 * @author Almex
 */
public class ListIterateItemWriter<T> implements ItemWriter<List<? extends T>> {

    private ItemWriter<T> delegate;

    @Override
    public void write(List<? extends List<? extends T>> items) throws Exception {
        for (List<? extends T> item : items) {
            delegate.write(item);
        }
    }

    public void setDelegate(ItemWriter<T> delegate) {
        this.delegate = delegate;
    }
}
