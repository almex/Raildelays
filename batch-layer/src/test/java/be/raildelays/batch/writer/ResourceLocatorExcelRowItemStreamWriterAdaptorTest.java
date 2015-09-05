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

package be.raildelays.batch.writer;

import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 */
public class ResourceLocatorExcelRowItemStreamWriterAdaptorTest {

    private ResourceLocatorExcelRowItemStreamWriterAdaptor writer;
    private ExcelRowResourceAwareItemWriterItemStream delegate;

    @Before
    public void setUp() throws Exception {
        writer = new ResourceLocatorExcelRowItemStreamWriterAdaptor();
        delegate = new ExcelRowResourceAwareItemWriterItemStream();
        writer.setDelegate(delegate);
        writer.setDirectoryPath(".\\");
        writer.setFileExtension(".xls");
        writer.setFileName("retard_sncb");
    }

    @Test
    public void testWrite() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        writer.open(executionContext);
        writer.write(Collections.singletonList(new ExcelRow
                .Builder(new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2000"), Sens.ARRIVAL)
                .build(false)));
        writer.update(executionContext);

        try {
            Assert.assertEquals(".\\retard_sncb 20000101.xls", delegate.getResource().getFile().getPath());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }

        writer.close();
    }

    private static class ExcelRowResourceAwareItemWriterItemStream implements ResourceAwareItemWriterItemStream<ExcelRow> {

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
        public void write(List<? extends ExcelRow> items) throws Exception {

        }
    }
}