package be.raildelays.httpclient

import be.raildelays.domain.Language
import be.raildelays.domain.Sens
import be.raildelays.httpclient.impl.DelaysRequest
import be.raildelays.httpclient.impl.DelaysRequestStreamer
import be.raildelays.httpclient.impl.DelaysRequestStreamerV2
import be.raildelays.httpclient.impl.DelaysRequestV2
import be.raildelays.httpclient.impl.StationListRequest
import be.raildelays.httpclient.impl.StationListRequestStreamer
import org.junit.Test

import static org.junit.Assert.assertNotNull

class RequestStreamerV2IT {

    @Test(timeout = 15000L)
    void testGetDelays() {
        assertNotNull("The request should return a result", new DelaysRequestStreamerV2().stream(new DelaysRequestV2('415', new Date(), Language.EN)));
    }

}
