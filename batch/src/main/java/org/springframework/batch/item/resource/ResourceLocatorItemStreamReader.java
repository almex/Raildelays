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

import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

/**
 * This {@link ItemStreamReader} make the link between a {@link ResourceContext} and a {@link ResourceLocator} to
 * determine when to set the {@code Resource} of our delegate.
 * <p>
 * Any {@link ResourceLocator} that would be used by this {@code ItemStreamReader} should implement at least
 * {@link ResourceLocator#onOpen(ResourceContext)} due to the fact that a {@link ResourceAwareItemReaderItemStream}
 * cannot be initiated without having a {@link Resource} at the opening of the stream.
 * </p>
 *
 * @author Almex
 * @since 2.0
 */
public class ResourceLocatorItemStreamReader<S extends ResourceAwareItemStreamReader<T>, T>
        extends AbstractResourceLocatorItemStream<S, T>
        implements ItemStreamReader<T>, ResourceAwareItemStreamReader<T> {


    /**
     * {@inheritDoc}
     * <p>
     * Check if the {@link ResourceLocator#onRead(Object, ResourceContext)} event has changed our
     * {@link ResourceContext}.
     * </p>
     * <p>
     * This implementation will close the stream if the resource from the context has changed.
     * If the current resource in the context has not been consumed yet then another stream is opened.
     * While there is no more item to read from the current resource and that the {@link ResourceLocator} 
     * changes the context, we delegate a new resource coming from that context.
     * </p>
     */
    @Override
    public T read() throws Exception {
        T item = null;

        // While we have consumed the current resource we continues with another one.
        while (item == null) {
            // In case it's not done yet, we delegate the resource coming from the context.
            if (!opened && resourceContext.containsResource()) {
                // That means we open a new stream
                delegate.open(resourceContext.getExecutionContext());
                opened = true;
            }

            // We check if the context has been initialized
            if (opened) {
                item = delegate.read();
                resourceLocator.onRead(item, resourceContext);
            }

            // We have finished to read the previous resource. We check if we don't have another one
            if (resourceContext.hasChanged()) {
                // That means we close the current stream.
                delegate.update(resourceContext.getExecutionContext());
                delegate.close();
                delegate.setResource(resourceContext.consumeResource());
                opened = false;
            } else {
                // We have no more resource to consume. No matter if we return a null item or not, we've done here.
                break;
            }
        }

        return item;
    }

}
