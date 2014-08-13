package be.raildelays.repository.specification;

import be.raildelays.domain.entities.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A class which is used to create {@link Specification} objects which are used
 * to create JPA criteria queries for {@link LineStop_}.
 *
 * @author Almex
 */
public class LineStopSpecifications {

    /**
     * Creates a specification used to find LineStop whose Station equals the expected one.
     *
     * @param station for which we should match the name
     * @return a predicate or null if all of name in each language are null
     */
    public static Specification<LineStop> stationEquals(final Station station) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Predicate predicate = null;
                Path<Station> path = root.get(LineStop_.station);

                if (StringUtils.isNotBlank(station.getEnglishName())) {
                    predicate = builder.and(builder.equal(path.get(Station_.englishName), station.getEnglishName()));
                } else if (StringUtils.isNotBlank(station.getFrenchName())) {
                    predicate = builder.and(builder.equal(path.get(Station_.frenchName), station.getFrenchName()));
                } else if (StringUtils.isNotBlank(station.getDutchName())) {
                    predicate = builder.and(builder.equal(path.get(Station_.dutchName), station.getDutchName()));
                }

                return predicate;
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose Train equals the expected one.
     *
     * @param train for which we should match the name
     * @return a predicate or null if all of name in each language are null
     */
    public static Specification<LineStop> trainEquals(final Train train) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Predicate predicate = null;
                Path<Train> path = root.get(LineStop_.train);

                if (StringUtils.isNotBlank(train.getEnglishName())) {
                    predicate = builder.and(builder.equal(path.get(Train_.englishName), train.getEnglishName()));
                } else if (StringUtils.isNotBlank(train.getFrenchName())) {
                    predicate = builder.and(builder.equal(path.get(Train_.frenchName), train.getFrenchName()));
                } else if (StringUtils.isNotBlank(train.getDutchName())) {
                    predicate = builder.and(builder.equal(path.get(Train_.dutchName), train.getDutchName()));
                }

                return predicate;
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose date equals the expected one.
     *
     * @param date for which we should have a match
     * @return a predicate
     */
    public static Specification<LineStop> dateEquals(final Date date) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get(LineStop_.date), date);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose arrival time is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> arrivalTimeIsNotNull() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return root.get(LineStop_.arrivalTime).get(TimestampDelay_.expected).isNotNull();
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose arrival time greater than the expected one.
     *
     * @param date the expected date
     * @return a predicate
     */
    public static Specification<LineStop> arrivalTimeGreaterThan(final Date date) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.greaterThan(root.get(LineStop_.arrivalTime).get(TimestampDelay_.expected), date);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose arrival delay is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> arrivalDelayIsNotNull() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return root.get(LineStop_.arrivalTime).get(TimestampDelay_.delay).isNotNull();
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose arrival delay is greater than the expected one.
     *
     * @param delay the expected delay
     * @return a predicate
     */
    public static Specification<LineStop> arrivalDelayGreaterThan(final Long delay) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.greaterThan(root.get(LineStop_.arrivalTime).get(TimestampDelay_.delay), delay);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose departure time is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> departureTimeIsNotNull() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return root.get(LineStop_.departureTime).get(TimestampDelay_.expected).isNotNull();
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose departure time greater than the expected one.
     *
     * @param date the expected date
     * @return a predicate
     */
    public static Specification<LineStop> departureTimeGreaterThan(final Date date) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.greaterThan(root.get(LineStop_.departureTime).get(TimestampDelay_.expected), date);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose departure delay is not null.
     *
     * @return a predicate
     */
    public static Specification<LineStop> departureDelayIsNotNull() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return root.get(LineStop_.departureTime).get(TimestampDelay_.delay).isNotNull();
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose departure delay is greater than the expected one.
     *
     * @param delay the expected delay
     * @return a predicate
     */
    public static Specification<LineStop> departureDelayGreaterThan(final Long delay) {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.greaterThan(root.get(LineStop_.departureTime).get(TimestampDelay_.delay), delay);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose are canceled.
     *
     * @return a predicate
     */
    public static Specification<LineStop> isCanceled() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get(LineStop_.canceled), true);
            }
        };
    }

    /**
     * Creates a specification used to find LineStop whose are canceled.
     *
     * @return a predicate
     */
    public static Specification<LineStop> isNotCanceled() {

        return new Specification<LineStop>() {
            @Override
            public Predicate toPredicate(Root<LineStop> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                return builder.equal(root.get(LineStop_.canceled), false);
            }
        };
    }

}
