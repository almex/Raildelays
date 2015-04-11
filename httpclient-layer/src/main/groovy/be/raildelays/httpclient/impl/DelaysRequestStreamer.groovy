package be.raildelays.httpclient.impl

import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.Stream
import be.raildelays.util.ParsingUtil

/**
 * Request a direction for a train to retrieve delays.
 *
 * @author Almex
 * @since 1.2
 */
public class DelaysRequestStreamer extends RailtimeRequestStreamer<DelaysRequest> {

    /**
     * @param idTrain train's id using Sncb format
     * @param day for which you want to retrieve delays
     * @param language specify 'eny', 'fny' or 'nny'
     * @return an HTML content as a stream to parse
     */
    @Override
    public Stream<DelaysRequest> stream(DelaysRequest request) {
        return new DefaultStream<DelaysRequest>(httpGet('/mobile/HTML/TrainDetail.aspx'
                , [l    : request.language.railtimeParameter
                   , tid: request.trainId
                   , dt : ParsingUtil.formatDate(request.day)
                   , da : request.sens.railtimeParameter])
                , request);
    }
}
