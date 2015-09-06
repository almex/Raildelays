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
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

import java.util.List;

/**
 * This {@link ItemStreamWriter} make the link between a {@link ResourceContext} and a {@link ResourceLocator} to
 * determine when to set the {@code Resource} of our delegate.
 *
 * @author Almex
 * @since 2.0
 */
public class ResourceLocatorItemWriterItemStream<T> implements ItemStreamWriter<T> {

    private ResourceAwareItemWriterItemStream<T> delegate;
    private ResourceContext resourceContext;
    private ResourceLocator resourceLocator;

    /**
     * {@inheritDoc}
     * <p>
     * The {@link ResourceContext} is initialized within this method based on the {@link ExecutionContext} provided.
     * Check if the  {@link ResourceLocator#onOpen(ResourceContext)} event has changed our
     * {@link ResourceContext}.
     * </p>
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        resourceContext = new ResourceContext(executionContext);

        resourceLocator.onOpen(resourceContext);

        if (resourceContext.hasChanged()) {
            delegate.setResource(resourceContext.consumeResource());
            delegate.open(executionContext);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     *     Check if the  {@link ResourceLocator#onUpdate(ResourceContext)} event has changed our
     *     {@link ResourceContext}.
     * </p>
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        resourceLocator.onUpdate(resourceContext);

        delegate.update(executionContext);

        if (resourceContext.hasChanged()) {
            delegate.close();
            delegate.setResource(resourceContext.consumeResource());
            delegate.open(resourceContext.getExecutionContext());
        }
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }


    /**
     * {@inheritDoc}
     * <p>
     *     Check if the {@link ResourceLocator#onWrite(List, ResourceContext)} event has changed our
     *     {@link ResourceContext}.
     * </p>
     */
    @Override
    public void write(List<? extends T> items) throws Exception {
        resourceLocator.onWrite(items, resourceContext);

        if (resourceContext.hasChanged()) {
            delegate.update(resourceContext.getExecutionContext());
            delegate.close();
            delegate.setResource(resourceContext.consumeResource());
            delegate.open(resourceContext.getExecutionContext());
        }

        delegate.write(items);
    }

    public void setDelegate(ResourceAwareItemWriterItemStream<T> delegate) {
        this.delegate = delegate;
    }

    public void setResourceLocator(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
}