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

import be.raildelays.httpclient.Request
import be.raildelays.httpclient.RequestStreamer
import groovyx.net.http.HTTPBuilder
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.Method.GET


abstract class AbstractRequestStreamBuilder<T extends Request> implements RequestStreamer<T> {

    def static
    final USER_AGENT = 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; custom; .NET CLR 1.1.4322; InfoPath.1; .NET CLR 2.0.50727; InfoPath.2; custom; custom)'

    def static final DEFAULT_LANGUAGE = 'en'

    def static Logger log = LoggerFactory.getLogger(AbstractRequestStreamBuilder.class)

    private String proxyHost;

    private Integer proxyPort;

    private String username;

    private String password;

    private String userAgent = USER_AGENT;

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * Do an HTTP GET to request a page.
     * Return the result as a stream to be parsed.
     *
     * @param path to your request
     * @param parameters of your request
     * @return return a {@link Reader}
     */
    protected Reader httpGet(String root, String path, Map parameters) {
        def final httpClient = new HTTPBuilder(root)
        def localUserAgent = userAgent

        if (proxyHost && proxyPort) {
            httpClient.setProxy(proxyHost, proxyPort, "http");

            if (username && password) {
                httpClient.client.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(username, password)
                );
            }
        }

        // perform a GET request, expecting plain/text response data
        httpClient.request(GET, TEXT) {
            uri.path = path
            uri.query = parameters

            headers.'User-Agent' = localUserAgent



            log.debug("URI=" + uri);

            // handler for any failure status code:
            response.failure = { resp ->
                log.error("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
            }
        }
    }

    /**
     * Do an HTTP GET to request a page.
     * Return the result as a stream to be parsed.
     *
     * @param path to your request
     * @param parameters of your request
     * @return return a {@link Reader}
     */
    protected abstract Reader httpGet(path, parameters);

}
