package be.raildelays.httpclient.impl

import be.raildelays.httpclient.Request
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static be.raildelays.util.ParsingUtil.formatDate
import static be.raildelays.util.ParsingUtil.formatTime

abstract class RailtimeRequestStreamer<T extends Request> extends AbstractRequestStreamBuilder<T> {

    def static Logger log = LoggerFactory.getLogger(RailtimeRequestStreamer.class)

    def static final ROOT = 'http://www.railtime.be';

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
//    public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour, String language = 'en') {
//        // http://www.railtime.be/mobile/HTML/RouteDetail.aspx?snd=Bruxelles-Central&std=215&sna=Li%C3%A8ge-Guillemins&sta=726&da=D&ti=00%3a02&sla=1&rca=21&rcb=0&l=EN&s=1
//        return httpGet('/mobile/HTML/RouteDetail.aspx', [snd: stationNameFrom, sna: stationNameTo, sta: 726, da: 'D', ti: formatTime(day), sla: 1, rca: 21, rcb: 0, l: DEFAULT_LANGUAGE, s: 1]);
//    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
//    public Reader getStationList(String language = 'en') {
//        throw new UnsupportedOperationException();
//    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
//    public Reader getDelays(String idTrain, Date day, String language = DEFAULT_LANGUAGE, String sens = 'A') {
//        return httpGet('/mobile/HTML/TrainDetail.aspx', [l: language, tid: idTrain, dt: formatDate(day), da: sens]);
//    }

    /**
     * {@inheritDoc}
     */
    //@Override FIXME Removed due to a bug since upgrade to Groovy 2.3.4
    protected Reader httpGet(path, parameters) {
        httpGet(ROOT, path, parameters)
    }
}
