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

package be.raildelays.batch.processor;

import be.raildelays.delays.Delays;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Filter delay greater than a certain threshold or filter delay less or equal to a certain depending on the
 * {@link Mode}.
 *
 * @author Almex
 * @since 1.2
 */
public class FilterWithThresholdDelayProcessor implements ItemProcessor<ExcelRow, ExcelRow>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger("Fas", FilterWithThresholdDelayProcessor.class);

    private Long threshold;
    private Mode mode;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(threshold, "The 'threshold' property must be provided");
    }

    @Override
    public ExcelRow process(final ExcelRow item) throws Exception {
        ExcelRow result;

        if (mode.filter(item, threshold)) {
            result = null;
            LOGGER.debug("filter:" + threshold, item);
        } else {
            result = item;
            LOGGER.debug("keep:" + threshold, item);
        }

        return result;
    }

    /**
     * @param threshold in number of minutes
     */
    public void setThreshold(Long threshold) {
        this.threshold = threshold;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public enum Mode {
        FILTER_LESS_THAN {
            @Override
            boolean filter(ExcelRow item, Long threshold) {
                return item.getDelay() < Delays.toMillis(threshold);
            }
        },
        FILTER_GREATER_OR_EQUAL_TO {
            @Override
            boolean filter(ExcelRow item, Long threshold) {
                return item.getDelay() >= Delays.toMillis(threshold);
            }
        };

        abstract boolean filter(ExcelRow item, Long threshold);
    }
}
