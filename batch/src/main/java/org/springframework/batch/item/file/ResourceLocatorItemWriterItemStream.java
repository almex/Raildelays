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

import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * This {@link ItemStreamWriter} make the link between a {@link ResourceContext} and a {@link ResourceLocator} to
 * determine when to set the {@code Resource} of our delegate.
 *
 * @author Almex
 * @since 2.0
 */
public class ResourceLocatorItemWriterItemStream<S extends ResourceAwareItemWriterItemStream<T>, T>
        extends AbstractResourceLocatorItemStream<S, T>
        implements ItemStreamWriter<T> {

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

    @Override
    public void setResourceToDelegate(Resource resource) {
        if (resourceContext != null) {
            resourceContext.changeResource(resource);
            delegate.setResource(resource);
        } else {
            throw new IllegalStateException("You must open the stream before calling setResourceToDelegate()");
        }
    }
}
