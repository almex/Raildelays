package be.raildelays.httpclient;

@FunctionalInterface
public interface RequestStreamer<T extends Request> {

    /**
     * Request
     *
     * @return an HTML content as a stream to parse
     */
    public Stream<T> stream(T request);
}
