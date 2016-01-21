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

import be.raildelays.delays.Delays
import be.raildelays.delays.TimeDelay
import be.raildelays.domain.Language
import be.raildelays.domain.entities.LineStop
import be.raildelays.domain.entities.Station
import be.raildelays.domain.entities.TrainLine
import be.raildelays.httpclient.Stream
import be.raildelays.httpclient.impl.DelaysRequestV2
import be.raildelays.parser.StreamParser
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.LocalTime

/**
 * Stream parser for data coming from HAFAS interface of SNCB/NMBS.
 *
 * @author Almex
 * @since 1.2
 */
class DelaysStreamParserV2 implements StreamParser<LineStop, DelaysRequestV2> {

    def slurper;
    List<List<Map>> json;

    private void init(Reader reader) {
        slurper = new JsonSlurper(type: JsonParserType.CHAR_BUFFER)
        json = slurper.parse(reader)

        assert json instanceof List
    }

    def Logger log = LoggerFactory.getLogger(DelaysStreamParserV2.class)

    @Override
    public LineStop parse(Stream<DelaysRequestV2> stream) {
        LineStop.Builder result = null;

        if (stream.reader != null) {
            init(stream.reader);
            result = map(stream.request);
        }

        return result != null ? result.build() : null;
    }

    private map(request) {
        LineStop.Builder result = null;

        json[0].forEach { object ->
            if (!"".equals(object.csAt) && !"".equals(object.csDt)) {
                LineStop.Builder builder = new LineStop.Builder();

                builder.date(request.day)
                        .train(getTrain(object, request.language))
                        .station(getStation(object, request.language))
                        .departureTime(getDepartureTime(object))
                        .arrivalTime(getArrivalTime(object))
                        .canceledDeparture(isCanceledDeparture(object))
                        .canceledArrival(isCanceledArrival(object));

                if (result == null) {
                    result = builder;
                } else {
                    result.addNext(builder);
                }
            }
        }

        return result;
    }

    private static TimeDelay getDepartureTime(Map object) {
        TimeDelay result = null;

        if (object.csDt != null) {
            result = TimeDelay.of(LocalTime.parse(object.csDt), object.dD != null ? Delays.toMillis(object.dD) : 0);
        }

        return result;
    }

    private static TimeDelay getArrivalTime(Map object) {
        TimeDelay result = null;

        if (object.csAt != null) {
            result = TimeDelay.of(LocalTime.parse(object.csAt), object.dA != null ? Delays.toMillis(object.dA) : 0);
        }

        return result;
    }

    private static Station getStation(Map object, Language language) {
        Station result = null;

        if (object.cs != null) {
            result = new Station(object.cs, language);
        }

        return result;
    }

    private static TrainLine getTrain(Map object, Language language) {
        TrainLine result = null;

        if (object.tNr != null) {
            result = new TrainLine.Builder(Long.parseLong(object.tNr)).build();
        }

        return result;
    }

    private static boolean isCanceledDeparture(Map object) {
        boolean departure = object.sD != null && object.sD == 1;

        return departure;
    }

    private static boolean isCanceledArrival(Map object) {
        boolean arrival = object.sA != null && object.sA == 1;

        return arrival;
    }

}
