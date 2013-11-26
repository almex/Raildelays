package be.raildelays.service;

import java.util.Date;
import java.util.List;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;

/**
 * Service used to collect data from Railtime and persist them.
 * 
 * @author Almex
 */
public interface RaildelaysService {

	/**
	 * Save served stops of a certain train for a certain date. Meaning that we
	 * have a scheduled time and delays this stop.
	 * 
	 * @param train
	 *            for which the timetable apply
	 * @param date
	 *            for which the timetable apply
	 * @param lineStop
	 *            linked with others to follow a direction
	 * @return the persisted {@link LineStop} linked with others to follow a
	 *         direction
	 */
	List<LineStop> saveRouteLog(final RouteLogDTO routeLog);

	/**
	 * Retrieve all delays between a station A to a station B for a certain
	 * date. This search is done no matter the direction of your ride (e.g.:
	 * from A to B/from B to a).
	 * 
	 * @param date
	 *            for which delays are recorded
	 * @param stationA
	 *            a certain station which could be your departure or arrival
	 *            station depending moment within the day
	 * @param stationB
	 *            a certain station which could be your departure or arrival
	 *            station depending moment within the day
	 * @param delayTreshold
	 *            minimum delay (in minutes)
	 * @return a list of {@link LineStop} linked each other following a
	 *         direction
	 */
	List<LineStop> searchDelaysBetween(final Date date, final Station stationA,
			final Station stationB, final int delayTreshold);

	/**
	 * Search all dates already stored within the database between two dates.
	 * 
	 * @param from
	 *            all dates after from
	 * @param to
	 *            all dates before to
	 * @return a list of unique {@link Date}
	 */
	List<Date> searchAllDates(Date from, Date to);

	/**
	 * Search all dates already stored within the database until a last date.
	 * 
	 * @param lastDate
	 *            all dates before to
	 * @return a list of unique {@link Date}
	 */
	List<Date> searchAllDates(Date lastDate);

	/**
	 * Search next train to go from A to B for a certain date.
	 * 
	 * @param station
	 *            station where you want to go
	 * @param date from which we do a search (format taken into account dd/MM/yyyy hh:mm)
	 * 			e.g.: next train must be the same day (dd/MM/yyyy) but after that time (hh:mm).
	 * @return a list of {@link LineStop} corresponding to the direction of the
	 *         next train
	 */
	List<LineStop> searchNextTrain(Station station, Date date);
	
	/**
	 * Search scheduling for a train line.
	 * 
	 * @param train for wich this line belongs
	 * @return a {@link LineStop} which does not contain effective time
	 */
	LineStop searchScheduledLine(Train train);
}