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

import org.springframework.core.io.Resource;

/**
 * Search the index of an item by reading a {@link Resource}
 *
 * @author Almex
 * @since 1.2
 */
public interface ResourceItemSearch<T extends Comparable<? super T>> {

    int EOF = -1;

    /**
     * Return the index of an {@code item} by searching it in a {@code resource}
     *
     * @param item     item you are looking for
     * @param resource resource which will be used by the reader
     * @return the index of the {@code item} or {@link #EOF} if not found.
     * @throws Exception in any case of error
     */
    int indexOf(T item, Resource resource) throws Exception;

    /**
     * This reader should be used in the {@link #indexOf(Comparable, Resource)} method to do the search.
     *
     * @param reader for which we do not provide a {@code resource}
     */
    void setReader(IndexedResourceAwareItemStreamReader<T> reader);
}
