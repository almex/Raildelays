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

import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.httpclient.impl.DelaysRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Composition of {@link FlatFileItemReader} and two {@link ScraperItemReader}.
 * <p>
 * This reader is restartable from the last FAILED {@link Chunk}.
 *
 * @author Almex
 */
public class CompositeRailtimeItemReader extends CompositeItemStream implements ItemReader<TwoDirections>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompositeRailtimeItemReader.class);

    private ScraperItemReader<Direction, DelaysRequest> departureReader;

    private ScraperItemReader<Direction, DelaysRequest> arrivalReader;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(arrivalReader, "You must provide a arrivalReader");
        Assert.notNull(departureReader, "You must provide a departureReader");

        LOGGER.debug("Reader initialized with arrivalReader={} and departureReader={}", arrivalReader, departureReader);
    }

    public TwoDirections read() throws Exception {
        TwoDirections result = null;

        Direction departureDirection = departureReader.read();
        Direction arrivalDirection = arrivalReader.read();

        if (departureDirection != null && arrivalDirection != null) {
            result = new TwoDirections(departureDirection, arrivalDirection);
        }

        return result;
    }

    public void setArrivalReader(ScraperItemReader arrivalReader) {
        this.arrivalReader = arrivalReader;
    }

    public void setDepartureReader(ScraperItemReader departureReader) {
        this.departureReader = departureReader;
    }

}
