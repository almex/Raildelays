package be.raildelays.parser

import java.util.Date;

import groovyx.net.http.*
import be.raildelays.domain.railtime.Direction

/**
 * Interface provided to parse content coming a stream.
 * 
 * @author Almex
 */
interface StreamParser {
	
	/**
	 * Parse a stream to create a {@link Direction} bean.
	 * 
	 * @param date for which you have made the request
	 * @param idTrain train's id following Railtime format for which you have made a request.
	 * @return a {@link Direction}
	 */
	public Direction parseDelay(String idTrain, Date date);
	
}
