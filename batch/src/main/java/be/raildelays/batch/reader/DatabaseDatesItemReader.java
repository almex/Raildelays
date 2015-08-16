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

import be.raildelays.service.RaildelaysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Search all dates stored in the database starting from the last date.
 *
 * @author Almex
 */
public class DatabaseDatesItemReader extends AbstractItemCountingItemStreamItemReader<Date> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DatabaseDatesItemReader.class);

    @Resource
    private RaildelaysService service;

    private Date lastDate;

    private List<Date> dates;

    /**
     * Default constructor.
     */
    public DatabaseDatesItemReader() {
        super();
        setName(ClassUtils.getShortName(DatabaseDatesItemReader.class));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Validate all job parameters
        Assert.notNull(lastDate, "You must provide the lastDate parameter to this Reader.");
    }

    @Override
    protected void doOpen() throws Exception {
        LOGGER.debug("Opening the stream with for dates until {}", lastDate);

        dates = service.searchAllDates(lastDate);

        LOGGER.debug("Retrieved {} dates", dates.size());
    }

    @Override
    protected void doClose() throws Exception {
        LOGGER.debug("Closing the stream...");
    }

    @Override
    protected Date doRead() throws Exception {
        Date result = null;

        if (getCurrentItemCount() <= dates.size()) {
            result = dates.get(getCurrentItemCount() - 1);

            LOGGER.debug("Reading one more date={}", result);
        }

        return result;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

}
