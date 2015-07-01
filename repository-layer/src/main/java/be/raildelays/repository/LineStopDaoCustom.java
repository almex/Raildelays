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
import be.raildelays.domain.entities.Train;

import java.util.Date;
import java.util.List;

public interface LineStopDaoCustom {

    /**
     * Search a list of delayed or canceled arrival {@link LineStop} which belong to departure or
     * arrival station for a certain day.
     *
     * @param date           date for which you do the search
     * @param station        departure station
     * @param delayThreshold minimum delay (in minutes)
     * @return a collection of {@link LineStop} belonging to departure
     */
    List<LineStop> findDepartureDelays(Date date, Station station,
                                       long delayThreshold);

    /**
     * Search a list of delayed or canceled departure {@link LineStop} which belong to departure or
     * arrival station for a certain day.
     *
     * @param date           date for which you do the search
     * @param station        arrival station
     * @param delayThreshold minimum delay (in minutes)
     * @return a collection of {@link LineStop} belonging to arrival
     */
    List<LineStop> findArrivalDelays(Date date, Station station,
                                     long delayThreshold);

    /**
     * Search a the next trains which is expected to arrive after a certain
     * time.
     *
     * @param station for which you do a search
     * @param date    a line stops must be of the same day of the year of this date
     *                and must have the expected departure time greater than the the
     *                hour specified into that date
     * @return a list of line stops of the same day in order of expected arrival
     * time
     */
    List<LineStop> findNextExpectedArrivalTime(Station station, Date date);

    /**
     * Return the first {@link be.raildelays.domain.entities.LineStop } for a certain
     * {@link be.raildelays.domain.entities.Train} at a certain {@link be.raildelays.domain.entities.Station}.
     *
     * @param train   which stop the the <code>station</code>
     * @param station representing the stop of the line
     * @return the first line stop from a list ascending ordered by expected arrival time
     */
    LineStop findFistScheduledLine(Train train, Station station);

}