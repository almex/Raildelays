package be.raildelays.httpclient;

import java.io.Reader;

/**
 * @author Almex
 * @since 2.0
 */
public class DefaultStream<T extends Request> implements Stream<T> {

    private Reader reader;

    private T request;

    public DefaultStream(Reader reader, T request) {
        this.reader = reader;
        this.request = request;
    }

    @Override
    public Reader getReader() {
        return reader;
    }

    @Override
    public T getRequest() {
        return request;
    }
}
