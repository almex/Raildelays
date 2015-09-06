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
 * You can also determine if this resource {@link #hasAlreadyBeenInitialized()} or not.
 * The {@link Resource} is stored in the {@link ExecutionContext} that has been used to instantiate this context.
 *
 * @author Almex
 * @since 2.0
 * @see ResourceLocator
 */
public class ResourceContext {

    public static final String RESOURCE_KEY = "resource.key";

    private ExecutionContext executionContext;
    private boolean hasChanged = false;
    private boolean initialized = false;

    public ResourceContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    /**
     * Change the resource contained within this context and set the context as initialized if it has not been set yet.
     *
     * @param resource the resource you want to store in this context.
     */
    public void changeResource(Resource resource) {
        Resource previousResource = (Resource) executionContext.get(RESOURCE_KEY);

        initialized = true;

        if (previousResource != resource) {
            hasChanged = true;
            executionContext.put(RESOURCE_KEY, resource);
        }
    }

    /**
     * Consume the resource if it has previously been set via {@link #changeResource(Resource)}.
     *
     * @return the {@link Resource} if it contains one, {@code null} otherwise.
     */
    public Resource consumeResource() {
        Resource result = null;

        if (containsResource()) {
            hasChanged = false;
            result = (Resource) executionContext.remove(RESOURCE_KEY);
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
        return hasChanged;
    }

    /**
     * Check if the context contains a resource to consume.
     *
     * @return {@code true} if a call to {@link #changeResource(Resource)} has been done and not already consumed,
     * {@code false} otherwise.
     */
    public boolean containsResource() {
        return executionContext.containsKey(RESOURCE_KEY);
    }

    /**
     * Determine if the resource has already been initialized or not.
     * This method return the same result as {@link #consumeResource()} if the context is used for the first time.
     *
     * @return {@code true} if any call to {@link #changeResource(Resource)} has already been made,
     * {@code false} otherwise
     */
    public boolean hasAlreadyBeenInitialized() {
        return initialized;
    }

    /**
     * You can interact with the {@link ExecutionContext}
     *
     * @return the {@link ExecutionContext} used to build this {@link ResourceContext}.
     */
    public ExecutionContext getExecutionContext() {
        return executionContext;
    }
}
