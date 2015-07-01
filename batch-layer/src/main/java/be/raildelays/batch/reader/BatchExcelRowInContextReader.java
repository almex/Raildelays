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

package be.raildelays.batch.reader;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Sens;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Get the {@link be.raildelays.batch.bean.BatchExcelRow} retrieved from the <code>Step</code>
 * {@link org.springframework.batch.item.ExecutionContext}.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.StoreDelayGreaterThanThresholdInContextProcessor
 * @since 1.2
 */
public class BatchExcelRowInContextReader implements ItemStreamReader<BatchExcelRow>, InitializingBean {

    private String keyName;

    private IteratorItemReader<BatchExcelRow> delegate;


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(keyName, "The 'keyName' property must be provided");
    }

    @Override
    public BatchExcelRow read() throws Exception {
        return delegate.read();
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(keyName)) {
            Map<Sens, BatchExcelRow> map = (Map) executionContext.get(keyName);

            delegate = new IteratorItemReader<BatchExcelRow>(map.values());
        } else {
            throw new IllegalStateException("Context should contain a value with this key : '" + keyName + "'");
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        //NOOP
    }

    @Override
    public void close() throws ItemStreamException {
        //NOOP
    }
}
