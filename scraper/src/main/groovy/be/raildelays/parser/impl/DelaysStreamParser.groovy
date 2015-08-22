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

package be.raildelays.parser.impl

import be.raildelays.domain.railtime.Direction
import be.raildelays.domain.railtime.Step
import be.raildelays.domain.railtime.Train
import be.raildelays.httpclient.Stream
import be.raildelays.httpclient.impl.DelaysRequest
import be.raildelays.parser.StreamParser
import be.raildelays.util.ParsingUtil
import org.ccil.cowan.tagsoup.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalDate
import java.time.LocalDateTime

class DelaysStreamParser implements StreamParser<Direction, DelaysRequest> {


    public static final String TIMESTAMP_FORMAT = "dd/MM/yyyy hh:mm";
    def tagsoupParser;
    def slurper;
    def html;

    private void init(Reader reader) {
        tagsoupParser = new Parser()
        slurper = new XmlSlurper(tagsoupParser)
        html = slurper.parse(reader)
    }

    def Logger log = LoggerFactory.getLogger(DelaysStreamParser.class)

    /**
     * {@inheritDoc}
     */
    @Override
    public Direction parse(Stream<DelaysRequest> stream) {

        init(stream.reader)

        def steps = [] as List
        def body = html.body.'**'
        Direction direction = new Direction(new Train(stream.request.trainId))
        def title = body.find { it.name() == 'h1' }.text()

        log.debug("title=" + title)
        direction.setLibelle(title);

        // Parse the page
        int ordinance = 0;
        body.findAll { it.name() == 'tr' && it.@class.text().contains('rowHeightTraject') }.each { tr ->
            String station = tr.td[1].text()
            String hour = tr.td[2].text()
            String delay = tr.td[3].text()
            boolean canceled = tr.td[0].@class.text().contains("Deleted")

            log.debug("station=" + station)
            log.debug("hour=" + hour)
            log.debug("delay=" + delay)
            log.debug("canceled=" + canceled)

            Step step = new Step(ordinance, station, extractDateTime(stream.request.day, hour), parseDelay(delay), canceled)

            steps.add(step)
            ordinance++
        }
        direction.steps = steps

        return direction;
    }

    private LocalDateTime extractDateTime(LocalDate day, String hour) {
        LocalDateTime dateTime

        if (!"".equals(hour)) {
            dateTime = ParsingUtil.parseTimestamp(ParsingUtil.formatDate(day) + hour)
        }

        return dateTime
    }

    def static Long stringToLong(String value) {
        String target = value.trim()
        if (target.isInteger())
            return Long.parseLong(target)
    }

    def static String extractDelay(String value) {
        return value.toString().replaceAll("[^0-9:]", "")
    }

    def parseDelay(String value) {
        Long delay = stringToLong(extractDelay(value))

        if (delay == null) {
            List<String> tokens = value.tokenize(':');

            if (tokens.size() == 2) {
                delay = stringToLong(extractDelay(tokens.get(1)));
                delay += stringToLong(extractDelay(tokens.get(0))) * 60;
            }
        }

        return delay != null ? delay : 0
    }
}
