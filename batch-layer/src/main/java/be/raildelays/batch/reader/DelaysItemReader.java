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

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.service.RaildelaysService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * Search delays for train going from A to B or B to A for a certain date.
 *
 * @author Almex
 */
public class DelaysItemReader implements ItemReader<LineStop>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DelaysItemReader.class);

    @Resource
    private RaildelaysService service;

    private IteratorItemReader<LineStop> delegate;

    private String stationA;

    private String stationB;

    private Date date;

    private Integer threshold;

    private String language = Language.EN.name();

    @Override
    public void afterPropertiesSet() throws Exception {
        // Validate all job parameters
        Assert.notNull(stationA, "You must provide the stationA parameter to this Reader.");
        Assert.notNull(stationB, "You must provide the stationB parameter to this Reader.");
    }

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        List<LineStop> result = new ArrayList<>();
        Language lang = Language.valueOf(language.toUpperCase(Locale.US));

        LOGGER.debug("Searching delays for date={}", date);

        if (date != null) {
            result = service.searchDelaysBetween(date, new Station(stationA, lang), new Station(stationB, lang), threshold);
        }

        Collections.sort(result);

        delegate = new IteratorItemReader<LineStop>(result.iterator());
    }


    public LineStop read() throws Exception {
        return delegate.read();
    }

    public void setStationA(String stationA) {
        this.stationA = stationA;
    }

    public void setStationB(String stationB) {
        this.stationB = stationB;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
