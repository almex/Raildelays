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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public class ResourceContextTest {

    private ResourceContext context;
    private ExecutionContext executionContext;

    @Before
    public void setUp() throws Exception {
        executionContext = new ExecutionContext();
        context = new ResourceContext(executionContext, "foo");
    }

    /**
     * We expect that when changing the resource the context has changed and it's initialized.
     */
    @Test
    public void testChangeResource() throws Exception {
        context.changeResource(new FileSystemResource("./"));

        Assert.assertTrue(context.hasChanged());
        Assert.assertTrue(context.containsResource());
    }

    /**
     * We expect that by consuming the resource the context has not changed but it's still initialized.
     */
    @Test
    public void testConsumeResource() throws Exception {
        Resource expected = new FileSystemResource("./");
        Resource actual;

        context.changeResource(expected);
        actual = context.consumeResource();

        Assert.assertEquals(expected, actual);
        Assert.assertTrue(context.containsResource());
        Assert.assertFalse(context.hasChanged());
    }

    /**
     * We expect that the reference to the ExecutionContext has not changed.
     */
    @Test
    public void testGetExecutionContext() throws Exception {
        ExecutionContext actual = context.getExecutionContext();

        Assert.assertEquals(executionContext, actual);
    }
}