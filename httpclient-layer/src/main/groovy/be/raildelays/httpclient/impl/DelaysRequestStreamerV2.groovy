package be.raildelays.httpclient.impl

import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.Stream
import be.raildelays.util.ParsingUtil

import java.text.SimpleDateFormat

/**
 * Request a direction for a train to retrieve delays.
 *
 * @author Almex
 * @since 1.2
 */
class DelaysRequestStreamerV2 extends SncbRequestStreamer<DelaysRequestV2> {

    @Override
    Stream<DelaysRequestV2> stream(DelaysRequestV2 request) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");

        return new DefaultStream<DelaysRequestV2>(httpGet('/bin/trainsearch.exe/' + request.language.sncbParameter
                , [trainname: request.trainId, date : formatter.format(request.day), getTrainFromArchive: 'yes'])
                , request);
    }
}
