package org.springframework.batch.test;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;

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
