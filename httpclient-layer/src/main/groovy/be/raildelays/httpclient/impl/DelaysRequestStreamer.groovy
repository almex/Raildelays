package be.raildelays.httpclient.impl

import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.Stream
import be.raildelays.util.ParsingUtil

/**
 * Request a direction for a train to retrieve delays.
 *
 * @author Almex
 * @since 2.0
 */

public class DelaysRequestStreamer extends RailtimeRequestStreamer<DelaysRequest> {

    /**
     * @param idTrain train's id using Railtime format
     * @param day day for which you want to retrive
     * @param language specify 'EN', 'FR' or 'NL'
     * @param sens 'D' for departure or 'A' for arrival
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
