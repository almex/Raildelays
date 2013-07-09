package be.raildelays.httpclient.impl

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import java.io.Reader

import org.slf4j.Logger
import org.slf4j.LoggerFactory


abstract class AbstractRequestStreamBuilder {

	def static final USER_AGENT = 'Mozilla/5.0 Ubuntu/8.10 Firefox/3.0.4'

	def static final DEFAULT_LANGUAGE = 'en'

	def static Logger log = LoggerFactory.getLogger(AbstractRequestStreamBuilder.class)

	/**
	 * Do an HTTP GET to request a page.
	 * Return the result as a stream to be parsed.
	 *
	 * @param path to your request
	 * @param parameters of your request
	 * @return return a {@link Reader}
	 */
	protected Reader httpGet(root, path, parameters) {
		def final httpClient = new HTTPBuilder( root )
		
		// perform a GET request, expecting plain/text response data
		httpClient.request( GET, TEXT ) {
			uri.path = path
			uri.query = parameters
		
			headers.'User-Agent' = USER_AGENT
			
			log.debug("path="+path);
			log.debug("parameters="+parameters);
				
			// handler for any failure status code:
			response.failure = { resp ->
				log.error("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
			}
		}
	}
	
	/**
	 * Do an HTTP GET to request a page.
	 * Return the result as a stream to be parsed.
	 *
	 * @param path to your request
	 * @param parameters of your request
	 * @return return a {@link Reader}
	 */
	protected abstract Reader httpGet(path, parameters);

}
