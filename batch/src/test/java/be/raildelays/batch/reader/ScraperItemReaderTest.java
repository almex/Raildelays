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

import be.raildelays.httpclient.AbstractRequest;
import be.raildelays.httpclient.Request;
import be.raildelays.httpclient.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.NeverRetryPolicy;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.Reader;

public class ScraperItemReaderTest {

    private ScraperItemReader<java.util.stream.Stream<String>, AbstractRequest> reader;

    @Before
    public void setUp() throws Exception {
        reader = new ScraperItemReader();
        reader.setParser(stream -> new BufferedReader(stream.getReader()).lines());
        reader.setStreamer(request -> new Stream() {

            @Override
            public Reader getReader() {
                return new CharArrayReader("test".toCharArray());
            }

            @Override
            public Request getRequest() {
                return request;
            }
        });
        reader.setRequest(new AbstractRequest());
        reader.setRetryPolicy(new NeverRetryPolicy());
        reader.setBackOffPolicy(new NoBackOffPolicy());
        reader.afterPropertiesSet();
    }

    @Test
    public void testRead() throws Exception {
        Assert.assertTrue(reader.read().allMatch(line -> "test".equals(line)));
    }
}