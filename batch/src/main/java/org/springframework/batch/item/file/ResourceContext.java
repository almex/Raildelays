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
 * @author Almex
 * @since 2.0
 */
public class ResourceContext {

    public static final String RESOURCE_KEY = "resource.key";

    private ExecutionContext executionContext;
    private boolean hasChanged = false;
    private boolean initialized = false;


    public ResourceContext(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public void changeResource(Resource resource) {
        Resource previousResource = consumeResource();

        if (previousResource != resource) {
            hasChanged = true;
            executionContext.put(RESOURCE_KEY, resource);
        }
    }

    public Resource consumeResource() {
        hasChanged = false;
        initialized = true;
        return (Resource) executionContext.get(RESOURCE_KEY);
    }

    public boolean hasChanged() {
        return hasChanged;
    }

    public ExecutionContext getExecutionContext() {
        return executionContext;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
