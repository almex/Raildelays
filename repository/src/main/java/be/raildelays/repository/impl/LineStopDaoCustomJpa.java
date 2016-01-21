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
import be.raildelays.domain.entities.TrainLine;
import be.raildelays.repository.LineStopDaoCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static be.raildelays.repository.specification.LineStopSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@SuppressWarnings("unused") // Injected via Spring Data JPA
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(LineStopDaoCustomJpa.class);
    @PersistenceContext
    @SuppressWarnings("unused") // Injected via CDI
    private EntityManager entityManager;

    @Override
    public Page<LineStop> findDepartureDelays(LocalDate date, Station station, long delayThreshold, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Subquery<Long> canceled = query.subquery(Long.class);
        Subquery<Long> notCanceled = query.subquery(Long.class);
        Root<LineStop> canceledRoot = canceled.from(LineStop.class);
        Root<LineStop> notCanceledRoot = notCanceled.from(LineStop.class);

        LOGGER.debug("Searching delays for : date={} station={} threshold={}",
                date, station, delayThreshold);

        canceled.select(canceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(isCanceledDeparture())
                        .toPredicate(canceledRoot, query, builder));

        notCanceled.select(notCanceledRoot.get(LineStop_.id))
                .where(where(dateEquals(date))
                        .and(stationEquals(station))
                        .and(departureDelayIsNotNull())
                        .and(departureDelayGreaterThanOrEqualTo(delayThreshold))
                        .toPredicate(notCanceledRoot, query, builder));

        Page<LineStop> all = findAll(where(idsIn(canceled)).or(idsIn(notCanceled)), pageable);

        LOGGER.debug("Retrieved delays : size={}/{} elements={}/{} pages={}/{}",
                all.getContent().size(), all.getSize(),
                all.getNumberOfElements(), all.getTotalElements(),
                all.getNumber(), all.getTotalPages());

        return all;
    }

    @Override
    public List<LineStop> findDepartureDelays(LocalDate date, Station station, long delayThreshold) {
        return findDepartureDelays(date, station, delayThreshold, null).getContent();
    }

    @Override
    public Page<LineStop> findArrivalDelays(LocalDate date, Station station, long delayThreshold, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Subquery<Long> canceled = query.subquery(Long.class);
        Subquery<Long> notCanceled = query.subquery(Long.class);
        Root<LineStop> canceledRoot = canceled.from(LineStop.class);
        Root<LineStop> notCanceledRoot = notCanceled.from(LineStop.class);

        LOGGER.debug("Searching delays for : date={} station={} threshold={}",
                date, station, delayThreshold);

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

        Page<LineStop> all = findAll(where(idsIn(canceled)).or(idsIn(notCanceled)), pageable);

        LOGGER.debug("Retrieved delays : size={}/{} elements={}/{} pages={}/{}",
                all.getContent().size(), all.getSize(),
                all.getNumberOfElements(), all.getTotalElements(),
                all.getNumber(), all.getTotalPages());

        return all;
    }

    @Override
    public List<LineStop> findArrivalDelays(LocalDate date, Station station, long delayThreshold) {
        return findArrivalDelays(date, station, delayThreshold, null).getContent();
    }

    @Override
    public List<LineStop> findNextExpectedArrivalTime(Station station, LocalDateTime dateTime) {
        return findAll(where(dateEquals(dateTime.toLocalDate()))
                        .and(arrivalTimeIsNotNull())
                        .and(arrivalTimeGreaterThan(dateTime.toLocalTime()))
                        .and(stationEquals(station)),
                new Sort(Sort.Direction.ASC, "arrivalTime.expectedTime")
        );
    }

    @Override
    public LineStop findFistScheduledLine(TrainLine trainLine, Station station) {
        return findFirstOne(where(arrivalTimeIsNotNull())
                .and(departureTimeIsNotNull())
                .and(isNotCanceled())
                .and(stationEquals(station))
                .and(trainEquals(trainLine)));
    }

    private Page<LineStop> findAll(Specifications<LineStop> specifications, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        query = query.where(specifications.toPredicate(root, query, builder));

        if (pageable != null && pageable.getSort() != null) {
            query = query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));
        }

        TypedQuery<LineStop> typedQuery = entityManager.createQuery(query);

        return pageable == null ? new PageImpl<>(typedQuery.getResultList()) : readPage(typedQuery, pageable, specifications);
    }

    protected Page<LineStop> readPage(TypedQuery<LineStop> query, Pageable pageable, Specification<LineStop> specifications) {

        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(specifications));
        List<LineStop> content = total > pageable.getOffset() ? query.getResultList() : Collections.emptyList();

        return new PageImpl<>(content, pageable, total);
    }

    private static Long executeCountQuery(TypedQuery<Long> query) {
        Assert.notNull(query);

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private List<LineStop> findAll(Specifications<LineStop> specifications, Sort sort) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        return entityManager
                .createQuery(query
                                .where(specifications.toPredicate(root, query, builder))
                                .orderBy(QueryUtils.toOrders(sort, root, builder))
                ).getResultList();
    }

    protected TypedQuery<Long> getCountQuery(Specification<LineStop> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<LineStop> root = applySpecificationToCriteria(specification, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return entityManager.createQuery(query);
    }

    private <S> Root<LineStop> applySpecificationToCriteria(Specification<LineStop> specification, CriteriaQuery<S> query) {
        Assert.notNull(query);
        Root<LineStop> root = query.from(LineStop.class);

        if (specification == null) {
            return root;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        Predicate predicate = specification.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
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
