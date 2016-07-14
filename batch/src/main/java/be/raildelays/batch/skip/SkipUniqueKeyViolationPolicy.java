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

package be.raildelays.batch.skip;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;
import java.util.function.Function;

/**
 * This {@link org.springframework.batch.core.step.skip.SkipPolicy} is dedicated to determine if we have an exceptional
 * duplicate key exception coming from the database when we insert new {@code LineStop}.
 * The {@code Exception} that we should match would be the {@link org.hibernate.exception.ConstraintViolationException} or
 * the {@link java.sql.SQLIntegrityConstraintViolationException} but unfortunately Hibernate convert this {@code class} into
 * more generic one called {@link javax.persistence.PersistenceException}.
 *
 * @author Almex
 * @since 1.2
 */
public class SkipUniqueKeyViolationPolicy implements SkipPolicy {

    public static final String[] CONSTRAINT_NAMES = {
            "LineStopUniqueBusinessKeyConstraint".toUpperCase(),
            "TrainLineUniqueBusinessKeyConstraint".toUpperCase()
    };


    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        boolean result = false;

        if (skipCount < 0 || isExpectedViolation(t)) {
            result = true; // We gracefully skip in case where the caller of this method gives us skipCount<0
        }

        return result;
    }

    private static boolean isExpectedViolation(Throwable e) {
        boolean violated = false;

        if (e instanceof ConstraintViolationException) {
            // We must ignore accent (we use Local.ENGLISH for that) and case
            violated = matchAnyViolationNames(
                    ((ConstraintViolationException) e).getConstraintName().toUpperCase(Locale.ENGLISH)::equals
            );
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            // We must ignore accent (we use Local.ENGLISH for that) and case
            violated = matchAnyViolationNames(e.getMessage().toUpperCase(Locale.ENGLISH)::contains);
        } else if (e instanceof PersistenceException && e.getCause() != null ||
                e instanceof DataIntegrityViolationException) {
            /**
             * We are in the case where Hibernate encapsulate the Exception into a PersistenceException.
             * Then we must check recursively into causes.
             */
            violated = isExpectedViolation(e.getCause());
        }

        return violated;
    }

    private static boolean matchAnyViolationNames(Function<String, Boolean> check) {
        boolean match = false;

        for (String constraintName : CONSTRAINT_NAMES) {
            match = check.apply(constraintName);

            if (match) {
                break;
            }
        }

        return match;
    }

}
