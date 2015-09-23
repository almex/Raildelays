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

package org.springframework.batch.item.resource;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.Resource;

import java.util.Comparator;

/**
 * Default implementation of a {@link ResourceItemSearch} which uses an {@link IndexedResourceAwareItemStreamReader}
 * to find the index of an expected item.
 *
 * @author Almex
 * @since 1.2
 * @see #setComparator(Comparator)
 */
public class SimpleResourceItemSearch<T extends Comparable<? super T>> implements ResourceItemSearch<T> {

    private IndexedResourceAwareItemStreamReader<? extends T> reader;
    protected Comparator<? super T> comparator = Comparator.naturalOrder();

    public SimpleResourceItemSearch() {
    }

    @Override
    public int indexOf(T item, Resource resource) throws Exception {
        int result = EOF;

        this.reader.setResource(resource);
        reader.open(new ExecutionContext());

        try {
            for (T object = reader.read(); object != null; object = reader.read()) {
                if (comparator.compare(item, object) == 0) {
                    result = reader.getCurrentIndex();
                    break;
                }
            }
        } finally {
            reader.close();
        }

        return result;
    }

    public void setReader(IndexedResourceAwareItemStreamReader<T> reader) {
        this.reader = reader;
    }

    /**
     * @param comparator used to know if the read item match the expected one. By default, we compare via the
     * {@link Comparable#compareTo(Object)} interface method.
     */
    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
}
