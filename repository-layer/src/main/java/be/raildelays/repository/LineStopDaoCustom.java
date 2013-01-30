package be.raildelays.repository;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

public interface LineStopDaoCustom {

	
	/**
	 * Search all dates containing a line stop already stored in the database.
	 * 
	 * @param after all dates returned must be greater than after
	 * @return a list of {@link Date}
	 */
	List<Date> findAllUniqueDates(Date after);
	
	/**
	 * Search all dates containing a line stop already stored in the database.
	 * 
	 * @return a list of {@link Date}
	 */
	List<Date> findAllUniqueDates();

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