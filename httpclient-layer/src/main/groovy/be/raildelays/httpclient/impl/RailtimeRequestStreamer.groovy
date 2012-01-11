package be.raildelays.httpclient.impl

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*

import java.io.OutputStream
import java.util.Date

import org.apache.log4j.Logger

import be.raildelays.httpclient.RequestStreamer
import static be.raildelays.util.ParsingUtil.*

class RailtimeRequestStreamer implements RequestStreamer {
	
	def static log = Logger.getLogger(RailtimeRequestStreamer.class)
		
	def static final USER_AGENT = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'
	
	def static final DEFAULT_LANGUAGE = 'en'
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getTrainList(String stationNameFrom, String stationNameTo, Date day, Integer hour) {
		// http://www.railtime.be/mobile/HTML/RouteDetail.aspx?snd=Bruxelles-Central&std=215&sna=Li%C3%A8ge-Guillemins&sta=726&da=D&ti=00%3a02&sla=1&rca=21&rcb=0&l=EN&s=1
		return httpGet('http://www.railtime.be', '/mobile/HTML/RouteDetail.aspx', [snd: stationNameFrom, sna: stationNameTo, sta: 726, da: 'D', ti: formatTime(day), sla: 1, rca: 21, rcb: 0, l: DEFAULT_LANGUAGE, s: 1]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getStationList() {
		return httpGet('http://hari.b-rail.be', '/infsta/StationList.ashx', [lang: '1']);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reader getDelays(String idTrain, Date day) {
		return httpGet('http://www.railtime.be', '/mobile/HTML/TrainDetail.aspx', [ l:DEFAULT_LANGUAGE, tid: idTrain, dt: formatDate(day) ]);
	}
	
	/**
	 * Do an HTTP GET to request a page.
	 * Return the result as a stream to be parsed.
	 * 
	 * @param root of your website
	 * @param path to your request
	 * @param parameters of your request
	 * @return return a {@link Reader}
	 */
	private Reader httpGet(root, path, parameters) {
		def final httpClient = new HTTPBuilder( root )
		
		// perform a GET request, expecting plain/text response data
		httpClient.request( GET, TEXT ) {
			uri.path = path
			uri.query = parameters
		
			headers.'User-Agent' = USER_AGENT
		
			// response handler for a success response code:
			/*response.success = { resp, reader ->
				assert resp.status == 200
				
				log.info("My response handler got response: ${resp.statusLine}")
				log.info("Response length: ${resp.headers.'Content-Length'}")
				
				return reader
			}*/
		
			// handler for any failure status code:
			response.failure = { resp ->
				log.error("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
			}
		}
		
		/*try {
			return new HTTPBuilder(root).request(GET,TEXT) {}
		}
		catch ( HttpResponseException ex ) {
			log.error("Unexpected error: ${ex.message}")
		}*/
	}
	
	/**
	* Do an REST POST to request a page.
	* Return the result as a stream to be parsed.
	*
	* @param root of your website
	* @param path to your request
	* @param parameters of your request
	* @return return a {@link Reader}
	*/
   private Reader restPost(root, path, xml) {
	   def final httpClient = new HTTPBuilder( root )
	   
	   // perform a POST request, expecting XML response data
	   httpClient.request( POST, XML ) {
		   
	   
		   headers.'User-Agent' = USER_AGENT
	   
		   body = {xml}
		   
		   // response handler for a success response code:
		   response.success = { resp, reader ->
			   assert resp.status == 200
			   
			   log.info("My response handler got response: ${resp.statusLine}")
			   log.info("Response length: ${resp.headers.'Content-Length'}")
			   log.debug(reader)
			   
			   return reader
		   }
	   
		   // handler for any failure status code:
		   response.failure = { resp ->
			   log.error("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
		   }
	   }
   }
	
}
