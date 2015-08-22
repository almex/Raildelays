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

package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.item.ExecutionContext;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 * @see be.raildelays.batch.support.ToWriteExcelResourceLocator
 */
public class ResourceLocatorListener {

    public static final String FILENAME_SUFFIX_KEY = "resource.filename.suffix";

    private ExecutionContext context;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.context = stepExecution.getExecutionContext();
    }

    @BeforeWrite
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        if (!items.isEmpty()) {
            // Retrieve first element of what would be written
            BatchExcelRow item = items.get(0);

            // We could have an empty row (i.e.: date can be null)
            if (item.getDate() != null) {
                String suffix = item.getDate().format(DateTimeFormatter.ISO_DATE);

                context.putString(FILENAME_SUFFIX_KEY, suffix);
            }
        }
    }
}
