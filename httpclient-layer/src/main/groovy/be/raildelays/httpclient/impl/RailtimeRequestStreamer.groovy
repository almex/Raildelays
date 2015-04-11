package be.raildelays.httpclient.impl

import be.raildelays.httpclient.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public abstract class RailtimeRequestStreamer<T extends Request> extends AbstractRequestStreamBuilder<T> {

    def static Logger log = LoggerFactory.getLogger(RailtimeRequestStreamer.class)

    def static final ROOT = 'http://www.railtime.be';

    @Override
    protected Reader httpGet(path, parameters) {
        httpGet(ROOT, path, parameters)
    }
}
