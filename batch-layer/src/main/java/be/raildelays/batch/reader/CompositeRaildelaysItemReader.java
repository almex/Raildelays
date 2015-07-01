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
 */.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Composition between {@link DelaysItemReader} and {@link DatabaseDatesItemReader}.
 * <p>
 * This reader is restartable from the last FAILED {@link Chunk}.
 *
 * @author Almex
 */
public class CompositeRaildelaysItemReader extends CompositeItemStream implements ItemReader<List<LineStop>>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompositeRaildelaysItemReader.class);

    private DelaysItemReader delaysItemReader;

    private DatabaseDatesItemReader datesItemReader;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(delaysItemReader, "You must provide a DelaysItemReader");
        Assert.notNull(datesItemReader, "You must provide a DatabaseDatesItemReader");
        register(datesItemReader);
    }

    public List<LineStop> read() throws Exception, UnexpectedInputException,
            ParseException, NonTransientResourceException {
        List<LineStop> result = null; // The end of this reader is when we have no more date
        Date date = datesItemReader.read();

        if (date != null) {
            delaysItemReader.setDate(date);

            // At this point we must return a non null value to continue reading
            result = new ArrayList<>();

            for (LineStop lineStop = delaysItemReader.read(); lineStop != null; lineStop = delaysItemReader.read()) {
                if (lineStop != null) {
                    result.add(lineStop);
                }
            }


            LOGGER.debug("Found {} delays for {}", result.size(), date);
        }

        return result;
    }

    public void setDelaysItemReader(DelaysItemReader delaysItemReader) {
        this.delaysItemReader = delaysItemReader;
    }

    public void setDatesItemReader(DatabaseDatesItemReader datesItemReader) {
        this.datesItemReader = datesItemReader;
    }

}
