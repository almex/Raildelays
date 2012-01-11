package be.raildelays.httpclient

import static org.junit.Assert.assertNotNull

import org.junit.Before
import org.junit.Test

import be.raildelays.httpclient.impl.RailtimeRequestStreamer

class RequestStreamerTest {

	/**
	 * S.U.T.
	 */
	private RequestStreamer streamer;
		
	@Test(timeout=3000L)
	void testGetTrainList() {
		assertNotNull("The request should return a result", streamer.getTrainList('Bruxelles-Central','Liège-Guillemins', new Date(),0))
	}
	
	@Test(timeout=1500L)
	void testGetStationList() {
		assertNotNull("The request should return a result", streamer.getStationList())
	}
	
	@Test(timeout=1500L)
	void testGetDelays() {
		assertNotNull("The request should return a result", streamer.getDelays('466', new Date()))
	}
	
	@Before
	void setUp() {
		streamer = new RailtimeRequestStreamer()
	}
	
}
