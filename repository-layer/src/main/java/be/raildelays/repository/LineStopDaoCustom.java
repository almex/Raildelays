package be.raildelays.repository;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;

public interface LineStopDaoCustom {

//	/**
//	 * Search a list of line stops that belong to a direction for a certain day.
//	 * 
//	 * @param departure
//	 *            station where you are coming from
//	 * @param arrival
//	 *            station where you are going to
//	 * @param date
//	 *            day of the year for which you do the search
//	 * @return a list of line stop
//	 */
//	public List<LineStop> findByStation(Station departure, Station arrival,
//			Date date);
	
	/**
	 * Search a list of line stops that belong to a train for a certain day.
	 * 
	 * @param idTrain
	 *            train's id in Railtime format.
	 * @param date
	 *            day of the year for which you do the search
	 * @return a list of line stop
	 */
	List<LineStop> findByTrain(String idTrain, Date date);

}