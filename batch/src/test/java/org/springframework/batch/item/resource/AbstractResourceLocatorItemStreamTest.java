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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.test.SimpleResourceAwareItemStream;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * @author Almex
 */
@RunWith(BlockJUnit4ClassRunner.class)
public abstract class AbstractResourceLocatorItemStreamTest<
        Delegate extends SimpleResourceAwareItemStream,
        Delegator extends AbstractResourceLocatorItemStream<Delegate, String>
        > {

    public static final String KEY = "key";
    protected Delegator delegator;
    protected Delegate delegate;


    @Before
    public abstract void setUp() throws Exception;

    /**
     * We expect that the onOpen() method build a path by by retrieving a property from the execution context.
     */
    @Test
    public void testOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        executionContext.put(KEY, "foo");
        delegator.setResourceLocator(new CountingItemResourceLocator<String>() {
            @Override
            public void onOpen(ResourceContext context) throws ItemStreamException {
                context.changeResource(new FileSystemResource(executionContext.getString(KEY)));
            }
        });

        delegator.open(executionContext);

        Assert.assertEquals("foo", delegate.getResource().getFile().getPath());
    }

    @After
    public void tearDown() {
        delegator.close();
    }

    public abstract String doReadOrWrite(List<String> items) throws Exception;

}