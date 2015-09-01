/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package org.springframework.batch.item.support;

import org.springframework.batch.item.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * Composition of multiple {@link ItemStreamReader}.
 * </p>
 * <p>
 * All items are loaded in memory when {@linkplain #open(ExecutionContext) opening} the stream.
 * </p>
 *
 * @author Almex
 * @since 2.0
 */
public class CompositeItemStreamReader<T> implements ItemStreamReader<T> {

    private CompositeItemStream stream;
    private ListItemReader<T> reader;
    private List<ItemStreamReader<T>> delegates;
    private Comparator<T> comparator;
    private boolean sortItems = true;

    /**
     * {@inheritDoc}
     * <p>
     * Note: this specific implementation read all items from all {@code delegates} and load them in memory.
     * </p>
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        stream = new CompositeItemStream();

        if (delegates != null) {
            List<T> items = new ArrayList<>();

            // Register all readers as a stream
            delegates.forEach(stream::register);
            // Open all streams
            stream.open(executionContext);
            // Read all items from all readers in memory
            delegates.forEach(delegate -> {
                try {
                    for (T item = delegate.read(); item != null; item = delegate.read()) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    throw new ItemStreamException("We were not able to consume all readers", e);
                }
            });

            // Sort the result before delegating it
            if (sortItems) {
                Collections.sort(items, comparator);
            }

            // Delegate the reading
            reader = new ListItemReader<>(items);
        } else {
            // Nothing to read
            reader = new ListItemReader<>(Collections.emptyList());
        }
    }

    @Override
    public T read() throws Exception {
        return reader.read();
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        stream.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        stream.close();
    }

    /**
     * List of {@link ItemStreamReader} that you want to read.
     */
    public void setDelegates(List<ItemStreamReader<T>> delegates) {
        this.delegates = delegates;
    }

    /**
     * If {@link #sortItems} is set to {@code true} then this {@link Comparator} is used to sort items in memory.
     */
    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    /**
     * Decide if we sort items in memory or not.
     */
    public void setSortItems(boolean sortItems) {
        this.sortItems = sortItems;
    }
}
