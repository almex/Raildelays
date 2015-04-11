package be.raildelays.batch.reader;

import be.raildelays.httpclient.Request;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.batch.item.ItemReader} capable of retrieving data from <i>www.railtime.be</i> website.
 * This implementation use a {@link org.springframework.retry.RetryPolicy} to allow to configure upon which
 * {@link java.lang.Exception} you want to try multiple attempt to read. For instance, {@code IOException} would be a
 * good choice as it denote a problem during the HTTP connection (maybe Wi-Fi is off, you network is not fully
 * startup yet). Then aside to the {@link org.springframework.retry.RetryPolicy} you must also configure the
 * {@link org.springframework.retry.backoff.BackOffPolicy} in order to define what to do between two attempts (e.g.:
 * wait 5 seconds).
 * <p>
 * To retrieve data from Railtime, this reader need to know the {@code trainId}, the {@code date},
 * the {@code sens} and the {@code language}.
 * </p>
 * <p>
 * To be respectful of the website we attempt to read, this reader also wait between 1 and 5 seconds between two
 * reads. Then we avoid any Deny Of Service.
 * </p>
 */
public class ScraperItemReader<T, R extends Request> implements ItemReader<T>, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScraperItemReader.class);

    private RequestStreamer<R> streamer;

    private StreamParser<T, R> parser;

    private R request;

    private RetryPolicy retryPolicy;

    private BackOffPolicy backOffPolicy;

    private RetryTemplate retryTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Validate all job parameters
        Assert.notNull(parser, "The 'parser' property must have a value");
        Assert.notNull(request, "The 'request' property must have a value");
        Assert.notNull(streamer, "The 'streamer' property must have a value");
        Assert.notNull(retryPolicy, "The 'retryPolicy' property must have a value");
        Assert.notNull(backOffPolicy, "The 'backOffPolicy' property must have a value");
        retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
    }

    public T read() throws Exception {

        return retryTemplate.execute(new RetryCallback<T, Exception>() {
            @Override
            public T doWithRetry(RetryContext context) throws Exception {
                T result = null;

                if (request != null) {
                    LOGGER.debug("Requesting Railtime for {}", request);

                    waitRandomly();

                    result = parser.parse(streamer.stream(request));

                    request = null; // We consume read, then next time we will return null if no new request is provided
                }

                return result;
            }
        });

    }

    /**
     * Wait a certain period of time before processing. It's more respectful for
     * grabber to do so.
     * <p>
     * Wait between 1 and 5 seconds.
     *
     * @throws InterruptedException
     */
    private void waitRandomly() throws InterruptedException {
        long waitTime = 1000 + Math.round(5000L * Math.random());

        LOGGER.debug("Waiting " + (waitTime / 1000) + " seconds...");

        Thread.sleep(waitTime);
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public void setBackOffPolicy(BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
    }

    public void setStreamer(RequestStreamer<R> streamer) {
        this.streamer = streamer;
    }

    public void setParser(StreamParser<T, R> parser) {
        this.parser = parser;
    }

    public R getRequest() {
        return request;
    }

    public void setRequest(R request) {
        this.request = request;
    }
}
