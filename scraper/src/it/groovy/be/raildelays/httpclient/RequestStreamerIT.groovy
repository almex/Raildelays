package be.raildelays.httpclient

import be.raildelays.domain.Language
import be.raildelays.domain.Sens
import be.raildelays.httpclient.impl.DelaysRequest
import be.raildelays.httpclient.impl.DelaysRequestStreamer
import be.raildelays.httpclient.impl.StationListRequest
import be.raildelays.httpclient.impl.StationListRequestStreamer
import org.junit.Test

import static org.junit.Assert.assertNotNull

class RequestStreamerIT {

    @Test(timeout = 15000L)
    void testGetStationList() {
        assertNotNull("The request should return a result", new StationListRequestStreamer().stream(new StationListRequest(Language.EN)));
    }

    @Test(timeout = 15000L)
    void testGetDelays() {
        assertNotNull("The request should return a result", new DelaysRequestStreamer().stream(new DelaysRequest('466', new Date(), Sens.DEPARTURE, Language.EN)));
    }

}
