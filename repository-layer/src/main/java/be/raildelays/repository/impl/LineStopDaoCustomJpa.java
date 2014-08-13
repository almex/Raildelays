package be.raildelays.repository.impl;

import be.raildelays.domain.entities.*;
import be.raildelays.repository.LineStopDaoCustom;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.query.QueryUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.Date;
import java.util.List;

import static be.raildelays.repository.specification.LineStopSpecifications.*;
import static org.springframework.data.jpa.domain.Specifications.where;

@SuppressWarnings("unused") // Injected via Spring Data JPA
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

    @PersistenceContext
    @SuppressWarnings("unused") // Injected via CDI
    private EntityManager entityManager;

    //    @SuppressWarnings("unchecked")
//    @Override
//    public List<LineStop> findDepartureDelays(Date date, Station station,
//                                              long delayThreshold) {
//        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
//        Root<LineStop> root = query.from(LineStop.class);
//        Path<Long> delay = root.get(LineStop_.departureTime).get(TimestampDelay_.delay);
//        query.where(builder.equal(root.get(LineStop_.date), date),
//                builder.and(delay.isNotNull()),
//                builder.and(builder.greaterThanOrEqualTo(delay, delayThreshold)),
//                builder.and(equals(root, builder, station))
//        );
//
//        return entityManager.createQuery(query).getResultList();
//
//    }
    @Override
    public List<LineStop> findDepartureDelays(Date date, Station station,
                                              long delayThreshold) {
//        return findAll(where(dateEquals(date))
//                .and(departureDelayIsNotNull())
//                .and(departureDelayGreaterThan(delayThreshold))
//                .and(stationEquals(station)));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        Subquery<Long> canceled = query.subquery(Long.class);
        canceled.where(where(dateEquals(date))
                .and(stationEquals(station))
                .and(isCanceled())
                .toPredicate(root, query, builder));

        Subquery<Long> notCanceled = query.subquery(Long.class);
        notCanceled.where(where(dateEquals(date))
                .and(stationEquals(station))
                .and(departureDelayIsNotNull())
                .and(departureDelayGreaterThan(delayThreshold))
                .toPredicate(root, query, builder));

        query.where(builder.or(canceled.in(), notCanceled.in()));

        return entityManager.createQuery(query).getResultList();
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public List<LineStop> findArrivalDelays(Date date, Station station,
//                                            long delayThreshold) {
//        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
//        Root<LineStop> root = query.from(LineStop.class);
//        Path<Long> delay = root.get(LineStop_.arrivalTime).get(TimestampDelay_.delay);
//        query.where(builder.equal(root.get(LineStop_.date), date),
//                builder.and(delay.isNotNull()),
//                builder.and(builder.greaterThanOrEqualTo(delay, delayThreshold)),
//                builder.and(equals(root, builder, station))
//        );
//
//        return entityManager.createQuery(query).getResultList();
//    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LineStop> findArrivalDelays(Date date, Station station,
                                            long delayThreshold) {
//        return findAll(where(dateEquals(date))
//                .and(arrivalDelayIsNotNull())
//                .and(arrivalDelayGreaterThan(delayThreshold))
//                .and(stationEquals(station)));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        Subquery<Long> canceled = query.subquery(Long.class);
        canceled.where(where(dateEquals(date))
                .and(stationEquals(station))
                .and(isCanceled())
                .toPredicate(root, query, builder));

        Subquery<Long> notCanceled = query.subquery(Long.class);
        notCanceled.where(where(dateEquals(date))
                .and(stationEquals(station))
                .and(arrivalDelayIsNotNull())
                .and(arrivalDelayGreaterThan(delayThreshold))
                .toPredicate(root, query, builder));

        query.where(builder.or(canceled.in(), notCanceled.in()));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<LineStop> findNextExpectedArrivalTime(Station station, Date date) {

//        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
//        Root<LineStop> root = query.from(LineStop.class);
//
//        return entityManager
//                .createQuery(query
//                        .where(where(dateEquals(date))
//                                .and(arrivalTimeIsNotNull())
//                                .and(arrivalTimeGreaterThan(date))
//                                .and(stationEquals(station))
//                                .toPredicate(root, query, builder))
//                        .orderBy(builder.asc(root.get(LineStop_.arrivalTime).get(TimestampDelay_.expected))))
//                .getResultList();

        return findAll(where(dateEquals(date))
                .and(arrivalTimeIsNotNull())
                .and(arrivalTimeGreaterThan(date))
                .and(stationEquals(station)), new Sort(Sort.Direction.ASC, "arrivalTime.expected"));

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
