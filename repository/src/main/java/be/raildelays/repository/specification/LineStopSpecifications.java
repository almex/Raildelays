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

package be.raildelays.repository.specification;

import be.raildelays.delays.TimeDelay_;
import be.raildelays.domain.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

/**
 * A class which is used to create {@link Specification} objects which are used
 * to create JPA criteria queries for {@link LineStop_}.
 *
 * @author Almex
 */
public class LineStopSpecifications {

    private LineStopSpecifications() {
        // Not used
    }

    /**
     * Creates a specification used to find LineStop whose Station equals the expectedTime one.
     *
     * @param station for which we should match the name
     * @return a predicate or null if all of name in each language are null
     */
    public static Specification<LineStop> stationEquals(final Station station) {

        return (root, query, builder) -> {
            Predicate predicate = null;
            Path<Station> path = root.get(LineStop_.station);

            if (StringUtils.isNotBlank(station.getEnglishName())) {
                predicate = builder.and(builder.equal(builder.upper(path.get(Station_.englishName)),
                        station.getEnglishName().toUpperCase(Locale.ENGLISH)));
            } else if (StringUtils.isNotBlank(station.getFrenchName())) {
                predicate = builder.and(builder.equal(builder.upper(path.get(Station_.frenchName)),
                        station.getFrenchName().toUpperCase(Locale.ENGLISH)));
            } else if (StringUtils.isNotBlank(station.getDutchName())) {
                predicate = builder.and(builder.equal(builder.upper(path.get(Station_.dutchName)),
                        station.getDutchName().toUpperCase(Locale.ENGLISH)));
            }

            return predicate;
        };
    }

    /**
     * Creates a specification used to find LineStop whose TrainLine equals the expectedTime one.
     *
     * @param trainLine for which we should match the name
     * @return a predicate or null if all of name in each language are null
     */
    public static Specification<LineStop> trainEquals(final TrainLine trainLine) {

        return (root, query, builder) -> {
            Predicate predicate = null;
            Path<TrainLine> path = root.get(LineStop_.trainLine);

            if (trainLine.getRouteId() != null) {
                predicate = builder.and(builder.equal(path.get(TrainLine_.routeId), trainLine.getRouteId()));
            }

            return predicate;
        };
    }

    /**
     * Creates a specification used to find LineStop whose date equals the expectedTime one.
     *
     * @param date for which we should have a match
     * @return a predicate
     */
    public static Specification<LineStop> dateEquals(final LocalDate date) {
        return (root, query, builder) -> builder.equal(root.get(LineStop_.date), date);
    }

    /**
     * Creates a specification used to find LineStop whose arrival time is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> arrivalTimeIsNotNull() {
        return (root, query, builder) -> root.get(LineStop_.arrivalTime).get(TimeDelay_.expectedTime).isNotNull();
    }

    /**
     * Creates a specification used to find LineStop whose arrival time greater than the expectedTime one.
     *
     * @param time the expectedTime time
     * @return a predicate
     */
    public static Specification<LineStop> arrivalTimeGreaterThan(final LocalTime time) {
        return (root, query, builder) -> builder.greaterThan(root.get(LineStop_.arrivalTime).get(TimeDelay_.expectedTime), time);
    }

    /**
     * Creates a specification used to find LineStop whose arrival delay is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> arrivalDelayIsNotNull() {
        return (root, query, builder) -> root.get(LineStop_.arrivalTime).get(TimeDelay_.delay).isNotNull();
    }

    /**
     * Creates a specification used to find LineStop whose arrival delay is greater than the expectedTime one.
     *
     * @param delay the expectedTime delay
     * @return a predicate
     */
    public static Specification<LineStop> arrivalDelayGreaterThanOrEqualTo(final Long delay) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(LineStop_.arrivalTime).get(TimeDelay_.delay), delay);
    }

    /**
     * Creates a specification used to find LineStop whose arrival delay is greater than the expectedTime one.
     *
     * @param delay the expectedTime delay
     * @return a predicate
     */
    public static Specification<LineStop> departureDelayGreaterThanOrEqualTo(final Long delay) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get(LineStop_.departureTime).get(TimeDelay_.delay), delay);
    }

    /**
     * Creates a specification used to find LineStop whose departure delay is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> departureDelayIsNotNull() {
        return (root, query, builder) -> root.get(LineStop_.departureTime).get(TimeDelay_.delay).isNotNull();
    }

    /**
     * Creates a specification used to find LineStop whose departure time is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> departureTimeIsNotNull() {
        return (root, query, builder) -> root.get(LineStop_.departureTime).get(TimeDelay_.expectedTime).isNotNull();
    }

    /**
     * Creates a specification used to find LineStop whose are canceled.
     *
     * @return a predicate
     */
    public static Specification<LineStop> isCanceledDeparture() {
        return (root, query, builder) -> builder.equal(root.get(LineStop_.canceledDeparture), true);
    }

    /**
     * Creates a specification used to find LineStop whose are canceled.
     *
     * @return a predicate
     */
    public static Specification<LineStop> isCanceledArrival() {
        return (root, query, builder) -> builder.equal(root.get(LineStop_.canceledArrival), true);
    }

    /**
     * Creates a specification used to find LineStop whose are canceled.
     *
     * @return a predicate
     */
    public static Specification<LineStop> isNotCanceled() {
        return (root, query, builder) -> builder.and(
                builder.equal(root.get(LineStop_.canceledDeparture), false),
                builder.equal(root.get(LineStop_.canceledArrival), false)
        );
    }

    /**
     * Creates a specification where {@link LineStop#id} must be in a list provided by a sub-query.
     *
     * @param subQuery returning a list of Id's
     * @return a predicate
     */
    public static Specification<LineStop> idsIn(Subquery<Long> subQuery) {
        return (root, query, builder) -> builder.in(root.get(LineStop_.id)).value(subQuery);
    }

}
