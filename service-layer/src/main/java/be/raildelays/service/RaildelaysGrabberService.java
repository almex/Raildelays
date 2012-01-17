package be.raildelays.service;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;

/**
 * Service used to collect data from Railtime and store everything in a database.
 * 
 * @author Almex
 */
public interface RaildelaysGrabberService {

	/**
	 * Retrieve all data from Railtime to store a LineStop.
	 * Including translation and arrival/departure time.  
	 * 
	 * @param idTrainRailtime train's id in a Railtime format.
	 * @param date for which you want to retrieve the data.
	 * @return the a list of {@LineStop} which has been persisted. 
	 */
	public List<LineStop> grabTrainLine(String idTrainRailtime, Date date);
}