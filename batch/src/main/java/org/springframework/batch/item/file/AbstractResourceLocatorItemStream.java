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

package org.springframework.batch.item.file;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 * @since 2.0
 */
public abstract class AbstractResourceLocatorItemStream<S extends ItemStream, T> extends ItemStreamSupport {

    protected S delegate;
    protected ResourceContext resourceContext;
    protected ResourceLocator<T> resourceLocator;
    protected boolean opened;

    /**
     * {@inheritDoc}
     * <p>
     * Check if the  {@link ResourceLocator#onOpen(ResourceContext)} event has changed our
     * {@link ResourceContext}.
     * </p>
     * <p>
     * If the delegate is an {@link AbstractItemCountingItemStreamItemWriter} or an
     * {@link AbstractItemCountingItemStreamItemReader} then we set the current index with
     * {@link ResourceContext#getCurrentIndex()}.
     * </p>
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        resourceContext = new ResourceContext(
                executionContext,
                getExecutionContextKey(this.getClass().getSimpleName())
        );
        ;

        resourceLocator.onOpen(resourceContext);

        if (delegate instanceof AbstractItemCountingItemStreamItemWriter) {
            ((AbstractItemCountingItemStreamItemWriter) delegate).setCurrentItemIndex(
                    resourceContext.getCurrentIndex()
            );
        } else if (delegate instanceof AbstractItemCountingItemStreamItemReader) {
            ((AbstractItemCountingItemStreamItemReader) delegate).setCurrentItemCount(
                    resourceContext.getCurrentIndex()
            );
        }

        if (resourceContext.hasChanged()) {
            setResourceToDelegate(resourceContext.consumeResource());
            delegate.open(executionContext);
            opened = true;
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (opened) {
            delegate.close();
            opened = false;
        }
    }

    public abstract void setResourceToDelegate(Resource resource);

    public void setDelegate(S delegate) {
        this.delegate = delegate;
    }

    public void setResourceLocator(ResourceLocator<T> resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
}
