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

package be.raildelays.service;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
     * @param routeLog for which the timetable apply
     * @return the persisted {@link LineStop} linked with others to follow a
     * direction
     */
    List<LineStop> saveRouteLog(final RouteLogDTO routeLog);

    /**
     * Retrieve all delays between a station A to a station B for a certain
     * date. This search is done no matter the direction of your ride (e.g.:
     * from A to B/from B to a).
     *
     * @param delayThreshold minimum delay (in milliseconds)
     * @param date           for which delays are recorded
     * @param stationA       a certain station which could be your departure or arrival
     *                       station depending moment within the day
     * @param stationB       a certain station which could be your departure or arrival
     *                       station depending moment within the day
     * @return a list of {@link LineStop} linked each other following a
     * direction
     */
    List<LineStop> searchDelaysBetween(final LocalDate date, final Station stationA,
                                       final Station stationB, final long delayThreshold);

    /**
     * Search all dates already stored within the database between two dates.
     *
     * @param from all dates after from
     * @param to   all dates before to
     * @return a list of unique {@link LocalDate}
     */
    List<LocalDate> searchAllDates(LocalDate from, LocalDate to);

    /**
     * Search all dates already stored within the database until a last date.
     *
     * @param lastDate all dates before to
     * @return a list of unique {@link LocalDate}
     */
    List<LocalDate> searchAllDates(LocalDate lastDate);

    /**
     * Search next train to go from A to B for a certain dateTime.
     *
     * @param station  station where you want to go
     * @param dateTime from which we do a search (format taken into account dd/MM/yyyy hh:mm)
     *                 e.g.: next train must be the same day (dd/MM/yyyy) but after that time (hh:mm).
     * @return a list of {@link LineStop} corresponding to the direction of the
     * next train
     */
    List<LineStop> searchNextTrain(Station station, LocalDateTime dateTime);

    /**
     * Search scheduling for a train line.
     *
     * @param train   for which this line belongs
     * @param station to determine from which stop the returned result begins
     * @return a {@link LineStop} which does not contain effective time
     */
    LineStop searchScheduledLine(Train train, Station station);

    /**
     * Search LineStop corresponding to a train id and for a date.
     *
     * @param trainId for which you do a look-up
     * @param date    for which correspond one LineStop
     * @return null if not found or if one of its parameter is null otherwise return the expectedTime LineStop
     * @since 1.2
     */
    LineStop searchLineStopByTrain(Long trainId, LocalDate date);
}