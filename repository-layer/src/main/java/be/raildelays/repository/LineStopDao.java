package be.raildelays.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;

/**
 * Repository that manage storing a {@link LineStop}.
 * 
 * @author Almex
 */
public interface LineStopDao extends JpaRepository<LineStop, Long>, LineStopDaoCustom {

	/**
	 * Search a list of line stops that belong to a train for a certain day.
	 * 
	 * @param trainEnglishName
	 *            train's id in Railtime format.
	 * @param date
	 *            day of the year for which you do the search
	 * @return a list of line stop
	 */
	LineStop findByTrainAndDateAndStation(Train train, Date date, Station station);

	/**
	 * Search a list of line stops that belong to a train for a certain day.
	 * 
	 * @param trainEnglishName
	 *            train's id in Railtime format.
	 * @param date
	 *            day of the year for which you do the search
	 * @return a list of line stop
	 */
	List<LineStop> findByTrainAndDate(Train train, Date date);
}
