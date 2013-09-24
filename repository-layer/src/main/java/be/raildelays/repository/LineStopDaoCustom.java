package be.raildelays.repository;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;

public interface LineStopDaoCustom {

	/**
	 * Search all dates containing a line stop already stored in the database.
	 * 
	 * @param after
	 *            all dates returned must be greater or equals than after
	 * @param before
	 *            all dates returned must be smaller or equals than before
	 * @return a list of {@link Date}
	 */
	List<Date> findAllUniqueDates(Date after, Date before);

	/**
	 * Search all dates containing a line stop already stored in the database.
	 * 
	 * @param lastDate
	 *            all dates returned must be smaller or equals than last date
	 * @return a list of {@link Date}
	 */
	List<Date> findAllUniqueDates(Date lastDate);

	/**
	 * Search all dates containing a line stop already stored in the database.
	 * 
	 * @return a list of {@link Date}
	 */
	List<Date> findAllUniqueDates();

	/**
	 * Search a list of arrival delayed line stops that belong departure or
	 * arrival for a certain day.
	 * 
	 * @param date
	 *            date for which you do the search
	 * @param departure
	 *            departure station
	 * @param arrival
	 *            arrival station
	 * @param delayTreshold
	 *            minimum delay (in minutes)
	 * @return a collection of {@link LineStop} belonging to departure
	 */
	List<LineStop> findDepartureDelays(Date date, Station station,
			int delayThreshold);

	/**
	 * Search a list of departure delayed line stops that belong departure or
	 * arrival for a certain day.
	 * 
	 * @param date
	 *            date for which you do the search
	 * @param departure
	 *            departure station
	 * @param arrival
	 *            arrival station
	 * @param delayTreshold
	 *            minimum delay (in minutes)
	 * @return a collection of {@link LineStop} belonging to arrival
	 */
	List<LineStop> findArrivalDelays(Date date, Station station,
			int delayThreshold);

	/**
	 * Search a the next trains which is expected to arrive after a certain
	 * time.
	 * 
	 * @param station
	 *            for which you do a search
	 * @param date
	 *            a line stops must be of the same day of the year of this date
	 *            and must have the expected departure time greater than the the
	 *            hour specified into that date
	 * @return a list of line stops of the same day in order of expected arrival
	 *         time
	 */
	List<LineStop> findNextExpectedArrivalTime(Station station, Date date);

}