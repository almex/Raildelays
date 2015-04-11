package be.raildelays.httpclient.impl;

import be.raildelays.httpclient.Request;

import java.io.Reader;

/**
 * Do an HTTP get on http://sncb-siv.hafas.de/bin/trainsearch.exe
 *
 * @author Almex
 * @since 1.2
 */
public abstract class SncbRequestStreamer<T extends Request> extends AbstractRequestStreamBuilder<T> {

    private static final String ROOT = "http://sncb-siv.hafas.de/bin/trainsearch.exe";

    @Override
    protected Reader httpGet(path, parameters) {
        httpGet(ROOT, path, parameters)
    }
}
