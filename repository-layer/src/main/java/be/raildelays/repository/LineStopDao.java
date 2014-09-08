package be.raildelays.repository;


import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Repository that manage storing a {@link LineStop}.
 *
 * @author Almex
 */
public interface LineStopDao extends JpaRepository<LineStop, Long>, LineStopDaoCustom {

    /**
     * Search a list of line stops that belong to a train for a certain day.
     *
     * @param train train id in Railtime format.
     * @param date  day of the year for which you do the search
     * @return a list of line stop
     */
    LineStop findByTrainAndDateAndStation(Train train, Date date, Station station);

    /**
     * Search a list of line stops that belong to a train for a certain day.
     *
     * @param trainId train id coming from our internal respository.
     * @param date    day of the year for which you do the search
     * @return a list of line stop
     */
    @Query("SELECT o "
            + "FROM LineStop o "
            + "WHERE o.date = :date "
            + "AND o.train.id = :trainId ")
    LineStop findByTrainIdAndDate(@Param("trainId") Long trainId, @Param("date") Date date);


    /**
     * Search a list of line stops that belong to a train for a certain day.
     *
     * @param train for which we match its names.
     * @param date  day of the year for which you do the search
     * @return a list of line stop
     */
    List<LineStop> findByTrainAndDate(Train train, Date date);

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @param after  all dates returned must be greater or equals than after
     * @param before all dates returned must be smaller or equals than before
     * @return a list of {@link Date}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "WHERE o.date >= :after "
            + "AND o.date <= :before "
            + "ORDER BY o.date ASC")
    List<Date> findAllUniqueDates(@Param("after") Date after, @Param("before") Date before);

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @return a list of {@link Date}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "ORDER BY o.date ASC")
    List<Date> findAllUniqueDates();

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @param lastDate all dates returned must be smaller or equals than last date
     * @return a list of {@link Date}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "WHERE o.date <= :before "
            + "ORDER BY o.date ASC")
    List<Date> findAllUniqueDates(@Param("before") Date lastDate);
}