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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Almex
 */
public class MultiResourceToReadLocatorTest {

    private MultiResourceToReadLocator<String> locator;

    @Before
    public void setUp() throws Exception {
        locator = new MultiResourceToReadLocator<>();
        locator.setDirectory(new ClassPathResource("./"));
        locator.setFilter("*.xls");
    }

    /**
     * We expect that we have at least one *.xls file in our test-classpath. So, onOpen() should at least charge
     * the ResourceContext with one Resource.
     */
    @Test
    public void testOnOpen() throws Exception {
        ResourceContext resourceContext = new ResourceContext(new ExecutionContext(), "foo");

        locator.onOpen(resourceContext);

        Assert.assertTrue(resourceContext.containsResource());
    }

    /**
     * We expect that a onOpen() followed by a onRead() with non-null item return the same Resource.
     */
    @Test
    public void testOnRead() throws Exception {
        ResourceContext resourceContext = new ResourceContext(new ExecutionContext(), "foo");
        Resource expected;
        Resource actual;

        locator.onOpen(resourceContext);
        expected = resourceContext.getResource();
        locator.onRead("a", resourceContext);
        actual = resourceContext.getResource();

        Assert.assertTrue(resourceContext.containsResource());
        Assert.assertEquals(expected, actual);
    }

    /**
     * We expect that a onOpen() followed by a onRead() with null item different Resource (as we have, at least, 2 Excel
     * file in our test-classpath).
     */
    @Test
    public void testOnReadWithNullItem() throws Exception {
        ResourceContext resourceContext = new ResourceContext(new ExecutionContext(), "foo");
        Resource expected;
        Resource actual;

        locator.onOpen(resourceContext);
        expected = resourceContext.getResource();
        locator.onRead(null, resourceContext);
        actual = resourceContext.getResource();

        Assert.assertTrue(resourceContext.containsResource());
        Assert.assertNotEquals(expected, actual);
    }

    /**
     * We expect that two consecutive onOpen() followed by a read of all items (null item is interpreted as EOF)
     * give us the same last Resource.
     */
    @Test
    public void testRestart() throws Exception {
        ResourceContext resourceContext = new ResourceContext(new ExecutionContext(), "foo");
        Resource expected = null;
        Resource actual = null;

        locator.onOpen(resourceContext);
        locator.onRead(null, resourceContext);
        expected = resourceContext.getResource();
        locator.onOpen(resourceContext);
        locator.onRead(null, resourceContext);
        actual = resourceContext.getResource();

        Assert.assertTrue(resourceContext.containsResource());
        Assert.assertEquals(expected, actual);
    }
}