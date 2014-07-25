package be.raildelays.repository.impl;

import be.raildelays.domain.entities.*;
import be.raildelays.repository.LineStopDaoCustom;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SuppressWarnings("unused") // Injected via Spring Data JPA
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

    @PersistenceContext
    @SuppressWarnings("unused") // Injected via CDI
    private EntityManager entityManager;

    private static Predicate[] equals(Root<LineStop> root, CriteriaBuilder builder, Station station) {
        List<Predicate> predicates = new ArrayList<>();
        Path<Station> path = root.get(LineStop_.station);

        if (StringUtils.isNotBlank(station.getEnglishName())) {
            predicates.add(builder.and(builder.equal(path.get(Station_.englishName), station.getEnglishName())));
        } else if (StringUtils.isNotBlank(station.getFrenchName())) {
            predicates.add(builder.and(builder.equal(path.get(Station_.frenchName), station.getFrenchName())));
        } else if (StringUtils.isNotBlank(station.getDutchName())) {
            predicates.add(builder.and(builder.equal(path.get(Station_.dutchName), station.getDutchName())));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private static Predicate[] equals(Root<LineStop> root, CriteriaBuilder builder, Train train) {
        List<Predicate> predicates = new ArrayList<>();
        Path<Train> path = root.get(LineStop_.train);

        if (StringUtils.isNotBlank(train.getEnglishName())) {
            predicates.add(builder.and(builder.equal(path.get(Train_.englishName), train.getEnglishName())));
        } else if (StringUtils.isNotBlank(train.getFrenchName())) {
            predicates.add(builder.and(builder.equal(path.get(Train_.frenchName), train.getFrenchName())));
        } else if (StringUtils.isNotBlank(train.getDutchName())) {
            predicates.add(builder.and(builder.equal(path.get(Train_.dutchName), train.getDutchName())));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LineStop> findDepartureDelays(Date date, Station station,
                                              long delayThreshold) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Path<Long> delay = root.get(LineStop_.departureTime).get(TimestampDelay_.delay);
        query.where(builder.equal(root.get(LineStop_.date), date),
                builder.and(delay.isNotNull()),
                builder.and(builder.greaterThanOrEqualTo(delay, delayThreshold)),
                builder.and(equals(root, builder, station))
        );

        return entityManager.createQuery(query).getResultList();

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LineStop> findArrivalDelays(Date date, Station station,
                                            long delayThreshold) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);
        Path<Long> delay = root.get(LineStop_.arrivalTime).get(TimestampDelay_.delay);
        query.where(builder.equal(root.get(LineStop_.date), date),
                builder.and(delay.isNotNull()),
                builder.and(builder.greaterThanOrEqualTo(delay, delayThreshold)),
                builder.and(equals(root, builder, station))
        );

        return entityManager.createQuery(query).getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Date> findAllUniqueDates(Date after, Date before) {
        return (List<Date>) entityManager
                .createQuery(
                        "SELECT DISTINCT o.date "
                                + "FROM LineStop o "
                                + "WHERE o.date >= :after "
                                + "AND o.date <= :before "
                                + "ORDER BY o.date ASC")
                .setParameter("after", after)
                .setParameter("before", before)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Date> findAllUniqueDates() {
        return (List<Date>) entityManager
                .createQuery(
                        "SELECT DISTINCT o.date "
                                + "FROM LineStop o "
                                + "ORDER BY o.date ASC")
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Date> findAllUniqueDates(Date lastDate) {
        return (List<Date>) entityManager
                .createQuery(
                        "SELECT DISTINCT o.date "
                                + "FROM LineStop o "
                                + "WHERE o.date <= :before "
                                + "ORDER BY o.date ASC")
                .setParameter("before", lastDate)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LineStop> findNextExpectedArrivalTime(Station station, Date date) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
        Root<LineStop> root = query.from(LineStop.class);

        Path<Date> expected = root.get(LineStop_.arrivalTime).get(TimestampDelay_.expected);
        query.where(builder.equal(root.get(LineStop_.date), date),
                builder.and(expected.isNotNull()),
                builder.and(builder.greaterThan(expected, date)),
                builder.and(equals(root, builder, station))
        ).orderBy(builder.asc(expected));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public LineStop findFistScheduledLine(Train train, Station station) {
        try {

            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<LineStop> query = builder.createQuery(LineStop.class);
            Root<LineStop> root = query.from(LineStop.class);
            Path<Date> expectedArrivalTime = root.get(LineStop_.arrivalTime).get(TimestampDelay_.expected);
            List<Predicate> predicates = new ArrayList<>();

            predicates.addAll(Arrays.asList(equals(root, builder, station)));
            predicates.addAll(Arrays.asList(equals(root, builder, train)));

            query.where(expectedArrivalTime.isNotNull(),
                    builder.and(root.get(LineStop_.departureTime).get(TimestampDelay_.expected).isNotNull()),
                    builder.and(builder.equal(root.get(LineStop_.canceled), false)),
                    builder.and(predicates.toArray(new Predicate[predicates.size()]))
            ).orderBy(builder.asc(expectedArrivalTime));

            return entityManager.createQuery(query).setMaxResults(1).setFirstResult(0).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
