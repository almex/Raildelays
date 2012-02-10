package be.raildelays.service;

import java.util.Collection;
import java.util.Date;

import be.raildelays.domain.entities.LineStop;

/**
 * Service used to collect data from Railtime and persist them.
 * 
 * @author Almex
 */
public interface RaildelaysGrabberService {

	/**
	 * Retrieve all data from Railtime to store a LineStop. Including
	 * translation and arrival/departure time.
	 * 
	 * @param idTrainRailtime
	 *            train's id in a Railtime format.
	 * @param date
	 *            for which you want to retrieve the data (no time is taken into
	 *            account).
	 * @return the a list of {@LineStop} which has been persisted.
	 */
	public Collection<LineStop> grabTrainLine(String idTrainRailtime, Date date);
	


	/**
	 * Retrieve all data from Railtime to store a LineStop for today. Including
	 * translation and arrival/departure time.
	 * 
	 * @param idTrainRailtime
	 *            train's id in a Railtime format.
	 * @return the a list of {@LineStop} which has been persisted.
	 */
	public Collection<LineStop> grabTrainLine(String idTrainRailtime);
}