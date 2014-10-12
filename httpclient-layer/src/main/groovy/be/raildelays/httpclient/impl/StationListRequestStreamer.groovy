package be.raildelays.httpclient.impl

import be.raildelays.httpclient.DefaultStream
import be.raildelays.httpclient.Stream

/**
 * Created by xbmc on 11-10-14.
 */
class StationListRequestStreamer extends BRailRequestStreamer<StationListRequest> {

    @Override
    Stream<StationListRequest> stream(StationListRequest request) {
        return new DefaultStream<StationListRequest>(httpGet('/infsta/StationList.ashx'
                , [lang: request.language])
                , request);
    }
}
