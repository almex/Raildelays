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
import org.springframework.batch.test.SimpleResourceAwareItemReaderItemStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorItemReaderItemStreamTest extends AbstractResourceLocatorItemStreamTest<
        SimpleResourceAwareItemReaderItemStream,
        ResourceLocatorItemReaderItemStream<SimpleResourceAwareItemReaderItemStream, String>
        > {

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

    @Test
    public void testReadWOResourceAtOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        delegator.setResourceLocator(new SimpleResourceLocator<String>() {
            @Override
            public String onRead(String item, ResourceContext context) throws Exception {
                context.changeResource(new FileSystemResource(item));

                return super.onRead(item, context);
            }
        });

        delegator.open(executionContext);
        delegator.read();
        delegator.update(executionContext);

        Assert.assertNull(delegate.getResource());
    }

    @Test
    public void testReadWResourceAtOpen() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        ResourceContext resourceContext = new ResourceContext(executionContext, "foo.ResourceLocatorItemReaderItemStream");

        resourceContext.changeResource(new ClassPathResource("retard_sncb 20140522.xls"));

        delegator.setResourceLocator(new SimpleResourceLocator<String>() {
            @Override
            public String onRead(String item, ResourceContext context) throws Exception {
                context.changeResource(new FileSystemResource(item));

                return super.onRead(item, context);
            }
        });

        delegator.open(executionContext);
        delegator.read();
        delegator.update(executionContext);

        Assert.assertEquals("a", delegate.getResource().getFile().getPath());
    }
}