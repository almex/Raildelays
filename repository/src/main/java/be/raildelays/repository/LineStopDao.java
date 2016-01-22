/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.repository;


import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository that manage storing a {@link LineStop}.
 *
 * @author Almex
 */
public interface LineStopDao extends JpaRepository<LineStop, Long>, LineStopDaoCustom {

    /**
     * Search a list of line stops that belong to a trainLine for a certain day.
     *
     * @param trainLine trainLine id in Railtime format.
     * @param date  day of the year for which you do the search
     * @return a list of line stop
     */
    LineStop findByTrainLineAndDateAndStation(TrainLine trainLine, LocalDate date, Station station);

    /**
     * Search a list of line stops that belong to a trainLine for a certain day.
     *
     * @param trainId trainLine id coming from our internal respository.
     * @param date    day of the year for which you do the search
     * @return a list of line stop
     */
    @Query("SELECT o "
            + "FROM LineStop o "
            + "WHERE o.date = :date "
            + "AND o.trainLine.id = :trainId ")
    LineStop findByTrainLineIdAndDate(@Param("trainId") Long trainId, @Param("date") LocalDate date);


    /**
     * Search a list of line stops that belong to a trainLine for a certain day.
     *
     * @param trainLine for which we match its names.
     * @param date  day of the year for which you do the search
     * @return a list of line stop
     */
    List<LineStop> findByTrainLineAndDate(TrainLine trainLine, LocalDate date);

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @param after  all dates returned must be greater or equals than after
     * @param before all dates returned must be smaller or equals than before
     * @return a list of {@link LocalDate}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "WHERE o.date >= :after "
            + "AND o.date <= :before "
            + "ORDER BY o.date ASC")
    List<LocalDate> findAllUniqueDates(@Param("after") LocalDate after, @Param("before") LocalDate before);

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @return a list of {@link LocalDate}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "ORDER BY o.date ASC")
    List<LocalDate> findAllUniqueDates();

    /**
     * Search all dates containing a line stop already stored in the database.
     *
     * @param lastDate all dates returned must be smaller or equals than last date
     * @return a list of {@link LocalDate}
     */
    @Query("SELECT DISTINCT o.date "
            + "FROM LineStop o "
            + "WHERE o.date <= :before "
            + "ORDER BY o.date ASC")
    List<LocalDate> findAllUniqueDates(@Param("before") LocalDate lastDate);
}