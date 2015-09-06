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

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.domain.Sens;
import be.raildelays.domain.xls.ExcelRow;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 */
public class ResourceLocatorItemWriterItemStreamTest {

    public static final String RETARD_SNCB_20000101_XLS = ".\\retard_sncb 20000101.xls";
    private ResourceLocatorItemWriterItemStream writer;
    private ExcelRowResourceAwareItemWriterItemStream delegate;


    @Before
    public void setUp() throws Exception {
        writer = new ResourceLocatorItemWriterItemStream();
        delegate = new ExcelRowResourceAwareItemWriterItemStream();
        writer.setDelegate(delegate);
        writer.setResourceLocator(new SimpleResourceLocator<ExcelRow>() {

            @Override
            public void onWrite(List<? extends ExcelRow> items, ResourceContext context) {
                String suffix = items.get(0).getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                File file = ExcelFileUtils.getFile(new File("." + File.separator), "retard_sncb", suffix, ".xls");

                context.changeResource(new FileSystemResource(file));
            }
        });
    }

    @Test
    public void testWrite() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();

        writer.open(executionContext);
        writer.write(Collections.singletonList(new ExcelRow
                .Builder(LocalDate.parse("2000-01-01"), Sens.ARRIVAL)
                .build(false)));
        writer.update(executionContext);

        try {
            Assert.assertEquals(RETARD_SNCB_20000101_XLS, delegate.getResource().getFile().getPath());
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