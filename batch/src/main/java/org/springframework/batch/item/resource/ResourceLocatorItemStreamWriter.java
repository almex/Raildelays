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

import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link ItemStreamWriter} make the link between a {@link ResourceContext} and a {@link ResourceLocator} to
 * determine when to set the {@code Resource} of our delegate.
 *
 * @author Almex
 * @since 2.0
 */
public class ResourceLocatorItemStreamWriter<S extends ResourceAwareItemStreamWriter<T>, T>
        extends AbstractResourceLocatorItemStream<S, T>
        implements ItemStreamWriter<T>, ResourceAwareItemStreamWriter<T> {

    /**
     * {@inheritDoc}
     * <p>
     * Check if the {@link ResourceLocator#onWrite(Object, ResourceContext)} event has changed our
     * {@link ResourceContext}.
     * </p>
     */
    @Override
    public void write(List<? extends T> items) throws Exception {
        Map<Resource, List<T>> splitMap = new HashMap<>();

        // We initialize the first resource
        if (resourceContext.containsResource()) {
            splitMap.put(resourceContext.getResource(), new ArrayList<>());
        }

        // We split the current list of items into sub-list linked to one resource
        for (T item : items) {
            resourceLocator.onWrite(item, resourceContext);

            // Does the onWrite has changed our context
            if (resourceContext.hasChanged()) {
                splitMap.put(resourceContext.consumeResource(), new ArrayList<>());
            }

            // We add the item to the current resource list
            if (splitMap.containsKey(resourceContext.getResource())) {
                splitMap.get(resourceContext.getResource()).add(item);
            }
        }

        // Now we can get through all our resources and write all our sub-list of items
        for (Map.Entry<Resource, List<T>> entry : splitMap.entrySet()) {
            Resource resource = entry.getKey();
            List<T> subItems = entry.getValue();

            delegate.setResource(resource);

            if (!opened) {
                // If it's the first resource, it's already opened
                delegate.open(resourceContext.getExecutionContext());
                opened = true;
            }

            delegate.write(subItems);

            if (splitMap.size() > 1) {
                // If we have more than one resource, we must close the current one
                delegate.close();
                delegate.update(resourceContext.getExecutionContext());
                opened = false;
            }
        }
    }
}
