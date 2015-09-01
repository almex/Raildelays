package org.springframework.batch.item.support;

import org.springframework.batch.item.ItemReader;

/**
 * Allow to use an {@link ItemReader} where somewhere you need an {@code ItemStreamReader}.
 *
 * @param <T> type of the returned data that you get by calling the {@link #read()} method
 * @author Almex
 * @since 2.0
 * @see AbstractItemStreamItemReader
 * @see org.springframework.batch.item.ItemStreamReader
 */
public class ItemStreamItemReaderDelegator<T> extends AbstractItemStreamItemReader<T> {

    private ItemReader<T> delegate;

    public ItemStreamItemReaderDelegator(ItemReader<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T read() throws Exception {
        return delegate.read();
    }
}
