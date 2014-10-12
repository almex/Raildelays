package be.raildelays.httpclient.impl

import be.raildelays.httpclient.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BRailRequestStreamer<T extends Request> extends AbstractRequestStreamBuilder<T> {

    def static Logger log = LoggerFactory.getLogger(BRailRequestStreamer.class)

    def static final ROOT = 'http://hari.b-rail.be';

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    protected Reader httpGet(path, parameters) {
        httpGet(ROOT, path, parameters)
    }
}
