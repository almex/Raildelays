package be.raildelays.repository;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

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
	 * Search a list of arrival delayed line stops that belong departure or arrival for a certain day.
	 * 
	 * @param date date for which you do the search
	 * @param departure departure station
	 * @param arrival arrival station
	 * @param delayTreshold minimum delay (in minutes)
	 * @return a collection of {@link LineStop} belonging to departure
	 */
	List<LineStop> findDepartureDelays(Date date, Station station, int delayThreshold);
	/**
	 * Search a list of departure delayed line stops that belong departure or arrival for a certain day.
	 * 
	 * @param date date for which you do the search
	 * @param departure departure station
	 * @param arrival arrival station
	 * @param delayTreshold minimum delay (in minutes)
	 * @return a collection of {@link LineStop} belonging to arrival
	 */
	List<LineStop> findArrivalDelays(Date date, Station station, int delayThreshold);

}