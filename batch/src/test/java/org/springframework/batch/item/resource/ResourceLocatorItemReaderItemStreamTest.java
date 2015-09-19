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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.test.SimpleResourceAwareItemStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorItemReaderItemStreamTest extends AbstractResourceLocatorItemStreamTest<
        ResourceLocatorItemReaderItemStreamTest.SimpleResourceAwareItemReaderItemStream,
        ResourceLocatorItemReaderItemStream<ResourceLocatorItemReaderItemStreamTest.SimpleResourceAwareItemReaderItemStream, String>
        > {

    public static final String EXCEL_FILE_NAME = "retard_sncb 20140522.xls";
    public static final String EXCEL_FILE_NAME2 = "retard_sncb 20150812.xls";

    @Before
    public void setUp() throws Exception {
        delegate = new SimpleResourceAwareItemReaderItemStream();
        delegator = new ResourceLocatorItemReaderItemStream<>();
        delegator.setName("foo");
        delegator.setDelegate(delegate);
    }

    @Override
    public String doReadOrWrite(List<String> items) throws Exception {
        return delegate.read();
    }

    /**
     * We expect to read nothing because the resource was neither located onRead() nor onOpen().
     */
    @Test(timeout = 1000L)
    public void testReadWOResourceOnOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        delegator.setResourceLocator(new CountingItemResourceLocator<>());
        delegator.open(executionContext);
        delegator.read();
        delegator.update(executionContext);

        Assert.assertNull(delegate.getResource());
    }

    /**
     * We expect to read a resource that was located onOpen() and not onRead().
     */
    @Test(timeout = 1000L)
    public void testReadWResourceOnOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        delegator.setResourceLocator(new CountingItemResourceLocator<String>() {
            @Override
            public void onOpen(ResourceContext context) throws ItemStreamException {
                context.changeResource(new ClassPathResource(EXCEL_FILE_NAME2));
            }
        });
        delegator.open(executionContext);

        Resource onOpen = delegate.getResource();
        Assert.assertEquals(EXCEL_FILE_NAME2, onOpen.getFilename());

        delegator.read();
        delegator.update(executionContext);

        Resource onRead = delegate.getResource();
        Assert.assertEquals(onOpen, onRead);
    }

    /**
     * We expect to read a resource that was located before onOpen() and onRead().
     */
    @Test(timeout = 1000L)
    public void testReadSetResourceBeforeOnOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        // By matching the name prefix used to store key in the execution context we can initialize the resource context
        ResourceContext resourceContext = new ResourceContext(
                executionContext,
                "foo." + ResourceLocatorItemReaderItemStream.class.getSimpleName()
        );

        resourceContext.changeResource(new ClassPathResource(EXCEL_FILE_NAME));

        delegator.setResourceLocator(new CountingItemResourceLocator<>());
        delegator.open(executionContext);

        Resource onOpen = delegate.getResource();
        Assert.assertEquals(EXCEL_FILE_NAME, onOpen.getFilename());

        delegator.read();
        delegator.update(executionContext);

        Resource onRead = delegate.getResource();
        Assert.assertEquals(onOpen, onRead);
    }

    /**
     * We expect to read a resource that was located onRead(). To do so we read first another resource then we change
     * via onRead() (we need two read).
     */
    @Test(timeout = 1000L)
    public void testReadWResourceOnRead() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        // By matching the name prefix used to store key in the execution context we can initialize the resource context
        ResourceContext resourceContext = new ResourceContext(
                executionContext,
                "foo." + ResourceLocatorItemReaderItemStream.class.getSimpleName()
        );

        resourceContext.setResource(new ClassPathResource(EXCEL_FILE_NAME));

        delegator.setResourceLocator(new CountingItemResourceLocator<String>() {
            @Override
            public void onRead(String item, ResourceContext context) throws Exception {
                context.changeResource(new FileSystemResource(item));
            }
        });
        delegator.open(executionContext);
        delegator.read();
        delegator.read();
        delegator.update(executionContext);

        Assert.assertEquals("a", delegate.getResource().getFilename());
    }

    public static class SimpleResourceAwareItemReaderItemStream
            extends SimpleResourceAwareItemStream
            implements ResourceAwareItemReaderItemStream<String> {

        @Override
        public String read() throws Exception {
            return "a";
        }
    }
}