package be.raildelays.batch.skip;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * This {@link org.springframework.batch.core.step.skip.SkipPolicy} is dedicated to determine if we have an exceptional
 * duplicate key exception coming from the database when we insert new {@code LineStop}.
 * The {@code Exception} that we should match would be the {@link org.hibernate.exception.ConstraintViolationException} or
 * the {@link java.sql.SQLIntegrityConstraintViolationException} but unforunatly Hibernate convert this {@code class} into
 * more generic one called {@link javax.persistence.PersistenceException}.
 *
 * @author Almex
 * @since 1.2
 */
public class SkipUniqueKeyViolationPolicy implements SkipPolicy {

    private static final String CONSTRAINT_NAME = "LineStopUniqueBusinessKeyConstraint".toUpperCase();

    private static Class<? extends Throwable>[] expecteds = new Class[]{ConstraintViolationException.class,
            SQLIntegrityConstraintViolationException.class,
            PersistenceException.class};


    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        boolean result = true;

        if (skipCount < 0) {
            if (isExpectedException(t) && !isExpectedViolation(t)) {
                result = false;
            }
        }

        return result;
    }

    private static boolean isExpectedException(Throwable e) {
        boolean result = false;

        for (Class<? extends Throwable> expected : expecteds) {
            if (expected.isAssignableFrom(e.getClass())) {
                result = true;
                break;
            }
        }

        return result;
    }

    private static boolean isExpectedViolation(Throwable e) {
        boolean result = false;

        if (e instanceof ConstraintViolationException) {
            result = ((ConstraintViolationException) e).getConstraintName().toUpperCase().equals(CONSTRAINT_NAME);
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            result = e.getMessage().toUpperCase().contains(CONSTRAINT_NAME);
        } else if (e.getCause() != null) {
            result = isExpectedViolation(e.getCause());
        }

        return result;
    }


}
