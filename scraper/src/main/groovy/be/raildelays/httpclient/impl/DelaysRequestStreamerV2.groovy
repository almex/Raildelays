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

package be.raildelays.httpclient.impl

import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.Stream

import java.text.SimpleDateFormat

/**
 * Request a direction for a train to retrieve delays.
 *
 * @author Almex
 * @since 1.2
 */
class DelaysRequestStreamerV2 extends SncbRequestStreamer<DelaysRequestV2> {

    @Override
    Stream<DelaysRequestV2> stream(DelaysRequestV2 request) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");

        return new DefaultStream<DelaysRequestV2>(httpGet('/bin/trainsearch.exe/' + request.language.sncbParameter
                , [trainname: request.trainId, date : formatter.format(request.day), getTrainFromArchive: 'yes'])
                , request);
    }
}