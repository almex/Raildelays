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
import org.springframework.core.io.Resource;

/**
 * Context used by a {@code ResourceLocator} to determine if the resource reference {@linkplain #hasChanged} or not.
 * You can also determine if this context {@link #containsResource()} or not.
 * The {@link Resource} is stored in the {@link ExecutionContext} that has been used to instantiate this context.
 *
 * @author Almex
 * @since 2.0
 * @see ResourceLocator
 */
public class ResourceContext {

    public static final String RESOURCE_KEY = "resource";
    public static final String HAS_CHANGED_KEY = "resource.has.changed";
    public static final String CURRENT_INDEX_KEY = "current.index";
    private ExecutionContext executionContext;
    private String keyPrefix;

    public ResourceContext(ExecutionContext executionContext, String keyPrefix) {
        this.executionContext = executionContext;
        this.keyPrefix = keyPrefix;
        setHasChanged(false);
        setCurrentIndex(0);
    }

    /**
     * Change the resource contained within this context and set the context as initialized if it has not been set yet.
     *
     * @param resource the resource you want to store in this context.
     */
    public void changeResource(Resource resource) {
        Resource previousResource = getResource();

        if (previousResource == null || !previousResource.equals(resource)) {
            setCurrentIndex(0);
            setHasChanged(true);
            setResource(resource);
        }
    }

    /**
     * Consume the resource if it has previously been set via {@link #changeResource(Resource)}.
     *
     * @return the {@link Resource} if it contains one, {@code null} otherwise.
     */
    public Resource consumeResource() {
        Resource result = null;

        if (hasChanged()) {
            setHasChanged(false);
            result = getResource();
        }

        return result;
    }

    /**
     * Determine if the resource has changed since the last time we consumed it.
     *
     * @return {@code true} if a call to {@link #changeResource(Resource)} has been done since the last time we called
     * {@link #consumeResource()}, {@code false} otherwise.
     */
    public boolean hasChanged() {
        return (Boolean) executionContext.get(getKey(HAS_CHANGED_KEY));
    }


    private void setHasChanged(boolean hasChanged) {
        executionContext.put(getKey(HAS_CHANGED_KEY), hasChanged);
    }

    /**
     * Check if the context contains a resource.
     *
     * @return {@code true} if a call to {@link #changeResource(Resource)} has been done and not already consumed,
     * {@code false} otherwise.
     */
    public boolean containsResource() {
        return executionContext.containsKey(getKey(RESOURCE_KEY));
    }

    /**
     * @return the current {@link Resource} without consuming it if the context contains one, {@code null} otherwise.
     */
    public Resource getResource() {
        return (Resource) executionContext.get(getKey(RESOURCE_KEY));
    }

    public void setResource(Resource resource) {
        executionContext.put(getKey(RESOURCE_KEY), resource);
    }

    public void setCurrentIndex(int index) {
        executionContext.putInt(getKey(CURRENT_INDEX_KEY), index);
    }

    public void incrementIndex() {
        int index = getCurrentIndex();

        setCurrentIndex(++index);
    }

    public int getCurrentIndex() {
        return executionContext.getInt(getKey(CURRENT_INDEX_KEY));
    }

    /**
     * You can interact with the {@link ExecutionContext}
     *
     * @return the {@link ExecutionContext} used to build this {@link ResourceContext}.
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    private String getKey(String key) {
        return keyPrefix + "." + key;
    }
}
