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
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorItemWriterItemStreamTest {

    public static final String KEY = "key";
    private ResourceLocatorItemWriterItemStream<String> writer;
    private SimpleResourceAwareItemWriterItemStream delegate;


    @Before
    public void setUp() throws Exception {
        writer = new ResourceLocatorItemWriterItemStream();
        delegate = new SimpleResourceAwareItemWriterItemStream();
        writer.setDelegate(delegate);
    }

    /**
     * We expect that the onWrite() method build a path by appending items into one String.
     */
    @Test
    public void testWrite() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        writer.setResourceLocator(new SimpleResourceLocator<String>() {
            @Override
            public void onWrite(List<? extends String> items, ResourceContext context) throws ItemStreamException {
                context.changeResource(new FileSystemResource(Arrays.toString(items.toArray())));
            }
        });

        writer.open(executionContext);
        writer.write(Arrays.asList("a", "b", "c"));
        writer.update(executionContext);

        try {
            Assert.assertEquals("[a, b, c]", delegate.getResource().getFile().getPath());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        writer.close();
    }

    /**
     * We expect that the onOpen() method build a path by by retrieving a property from the execution context.
     */
    @Test
    public void testOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        executionContext.put(KEY, "foo");
        writer.setResourceLocator(new SimpleResourceLocator<String>() {
            @Override
            public void onOpen(ResourceContext context) throws ItemStreamException {
                context.changeResource(new FileSystemResource(executionContext.getString(KEY)));
            }
        });

        writer.open(executionContext);
        writer.write(Arrays.asList("a", "b", "c"));
        writer.update(executionContext);

        try {
            Assert.assertEquals("foo", delegate.getResource().getFile().getPath());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        writer.close();
    }

    /**
     * We expect that the onUpdate() method build a path by by retrieving a property from the execution context.
     */
    @Test
    public void testClose() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        executionContext.put(KEY, "foo");
        writer.setResourceLocator(new SimpleResourceLocator<String>() {
            @Override
            public void onUpdate(ResourceContext context) throws ItemStreamException {
                context.changeResource(new FileSystemResource(executionContext.getString(KEY)));
            }
        });

        writer.open(executionContext);
        writer.write(Arrays.asList("a", "b", "c"));
        writer.update(executionContext);

        try {
            Assert.assertEquals("foo", delegate.getResource().getFile().getPath());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        writer.close();
    }

    private static class SimpleResourceAwareItemWriterItemStream implements ResourceAwareItemWriterItemStream<String> {

        private Resource resource;

        public Resource getResource() {
            return resource;
        }

        @Override
        public void setResource(Resource resource) {
            this.resource = resource;
        }

        @Override
        public void open(ExecutionContext executionContext) throws ItemStreamException {

        }

        @Override
        public void update(ExecutionContext executionContext) throws ItemStreamException {

        }

        @Override
        public void close() throws ItemStreamException {

        }

        @Override
        public void write(List<? extends String> items) throws Exception {

        }
    }
}