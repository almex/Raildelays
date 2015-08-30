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

package be.raildelays.repository.impl;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.LineStop_;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDaoCustom;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static be.raildelays.repository.specification.LineStopSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@SuppressWarnings("unused") // Injected via Spring Data JPA
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

    @PersistenceContext
    @SuppressWarnings("unused") // Injected via CDI
    private EntityManager entityManager;

    @Override
    public List<LineStop> findDepartureDelays(LocalDate date, Station station, long delayThreshold) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Subquery<Long> canceled = query.subquery(Long.class);
        Subquery<Long> notCanceled = query.subquery(Long.class);
        Root<LineStop> canceledRoot = canceled.from(LineStop.class);
        Root<LineStop> notCanceledRoot = notCanceled.from(LineStop.class);

        canceled.select(canceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(isCanceledDeparture())
                        .toPredicate(root, query, builder));

        notCanceled.select(notCanceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(departureDelayIsNotNull())
                        .and(arrivalDelayGreaterThanOrEqualTo(delayThreshold))
                        .toPredicate(root, query, builder));

        query.where(builder.or(
                builder.in(root.get(LineStop_.id)).value(canceled),
                builder.in(root.get(LineStop_.id)).value(notCanceled)
        ));

        return entityManager.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LineStop> findArrivalDelays(LocalDate date, Station station, long delayThreshold) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Subquery<Long> canceled = query.subquery(Long.class);
        Subquery<Long> notCanceled = query.subquery(Long.class);
        Root<LineStop> canceledRoot = canceled.from(LineStop.class);
        Root<LineStop> notCanceledRoot = notCanceled.from(LineStop.class);

        canceled.select(canceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(isCanceledArrival())
                        .toPredicate(canceledRoot, query, builder));

        notCanceled.select(notCanceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(arrivalDelayIsNotNull())
                        .and(arrivalDelayGreaterThanOrEqualTo(delayThreshold))
                        .toPredicate(notCanceledRoot, query, builder));

        query.where(builder.or(
                builder.in(root.get(LineStop_.id)).value(canceled),
                builder.in(root.get(LineStop_.id)).value(notCanceled)
        ));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<LineStop> findNextExpectedArrivalTime(Station station, LocalDateTime dateTime) {
        return findAll(where(dateEquals(dateTime.toLocalDate()))
                .and(arrivalTimeIsNotNull())
                .and(arrivalTimeGreaterThan(dateTime.toLocalTime()))
                .and(stationEquals(station)), new Sort(Sort.Direction.ASC, "arrivalTime.expectedTime"));

    }

    @Override
    public LineStop findFistScheduledLine(Train train, Station station) {
        return findFirstOne(where(arrivalTimeIsNotNull())
                .and(departureTimeIsNotNull())
                .and(isNotCanceled())
                .and(stationEquals(station))
                .and(trainEquals(train)));
    }

    private List<LineStop> findAll(Specifications<LineStop> specifications) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        return entityManager
                .createQuery(query.where(specifications.toPredicate(root, query, builder)))
                .getResultList();
    }

    private List<LineStop> findAll(Specifications<LineStop> specifications, Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        return entityManager
                .createQuery(query
                        .where(specifications.toPredicate(root, query, builder))
                        .orderBy(QueryUtils.toOrders(sort, root, builder)))
                .getResultList();
    }

    private LineStop findFirstOne(Specifications<LineStop> specification) {
        try {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
            Root<LineStop> root = query.from(LineStop.class);

            return entityManager
                    .createQuery(query.where(specification.toPredicate(root, query, builder)))
                    .setMaxResults(1)
                    .setFirstResult(0)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
