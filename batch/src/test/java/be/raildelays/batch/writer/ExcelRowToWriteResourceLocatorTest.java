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
import org.springframework.batch.item.resource.ResourceContext;

import java.io.File;
import java.time.LocalDate;

/**
 * @author Almex
 */
public class ExcelRowToWriteResourceLocatorTest {

    private ExcelRowToWriteResourceLocator resourceLocator;
    private ResourceContext context;

    @Before
    public void setUp() throws Exception {
        context = new ResourceContext(new ExecutionContext(), "foo");
        resourceLocator = new ExcelRowToWriteResourceLocator();
        resourceLocator.setFileName("retard_sncb");
        resourceLocator.setFileExtension(".xls");
        resourceLocator.setDirectoryPath("./");
    }

    /**
     * We expect that the resource path will be './retard_sncb 20000101.xls'.
     */
    @Test
    public void testOnWrite() throws Exception {
        ExcelRow item = new ExcelRow
                        .Builder(LocalDate.of(2000, 1, 1), Sens.ARRIVAL)
                .build(false);

        resourceLocator.onWrite(item, context);

        Assert.assertEquals("." + File.separator + "retard_sncb 20000101.xls",
                context.consumeResource().getFile().getPath());
    }

    /**
     * We expect to get no resource when there is no item to write.
     */
    @Test
    public void testOnWriteNoItems() throws Exception {
        resourceLocator.onWrite(null, context);

        Assert.assertNull(context.consumeResource());
    }

    /**
     * We expect nothing.
     */
    @Test
    public void testOnOpen() throws Exception {
        resourceLocator.onOpen(context);
    }
}