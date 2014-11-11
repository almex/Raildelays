package be.raildelays.parser;

import be.raildelays.httpclient.Request;
import be.raildelays.httpclient.Stream;

/**
 * Interface provided to parse content coming from a {@link be.raildelays.httpclient.Stream}.
 *
 * @author Almex
 */
@FunctionalInterface
public interface StreamParser<T, R extends Request> {

    public T parse(Stream<R> stream);

}
