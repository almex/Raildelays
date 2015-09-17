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
import org.springframework.batch.test.SimpleResourceAwareItemWriterItemStream;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorItemWriterItemStreamTest extends AbstractResourceLocatorItemStreamTest<
        SimpleResourceAwareItemWriterItemStream,
        ResourceLocatorItemWriterItemStream<SimpleResourceAwareItemWriterItemStream, String>
        > {


    @Before
    public void setUp() throws Exception {
        delegator = new ResourceLocatorItemWriterItemStream();
        delegate = new SimpleResourceAwareItemWriterItemStream();
        delegator.setDelegate(delegate);
        delegator.setName("foo");
    }

    @Override
    public String doReadOrWrite(List<String> items) throws Exception {
        delegator.write(items);

        return null;
    }

    /**
     * We expect that the onWrite() method build a path by appending items into one String.
     */
    @Test
    public void testWrite() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        delegator.setResourceLocator(new CountingItemResourceLocator<String>() {
            @Override
            public void onWrite(String item, ResourceContext context) throws ItemStreamException {
                context.changeResource(new FileSystemResource(item));
            }
        });

        delegator.open(executionContext);
        delegator.write(Arrays.asList("a", "b", "c"));
        delegator.update(executionContext);

        try {
            Assert.assertEquals("c", delegate.getResource().getFile().getPath());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        delegator.close();
    }

}