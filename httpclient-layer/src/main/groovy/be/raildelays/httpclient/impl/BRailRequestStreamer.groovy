package be.raildelays.httpclient.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BRailRequestStreamer extends AbstractRequestStreamBuilder {

    def static Logger log = LoggerFactory.getLogger(BRailRequestStreamer.class)

    def static final DEFAULT_ROOT = 'http://hari.b-rail.be';

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour, String language = '1') {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    public Reader getStationList(String language = '1') {
        return httpGet('/infsta/StationList.ashx', [lang: language]);
    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    public Reader getDelays(String idTrain, Date day, String language = '1', String sens = 'A') {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    protected Reader httpGet(path, parameters) {
        httpGet(ROOT, path, parameters)
    }
}
