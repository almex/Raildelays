package be.raildelays.httpclient;

import java.io.Reader;

/**
 * @author Almex
 * @since 2.0
 */
public interface Stream<T extends Request> {

    Reader getReader();

    T getRequest();

}
