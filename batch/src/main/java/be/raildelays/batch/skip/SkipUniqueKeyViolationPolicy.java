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

import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Locale;

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

    public static final String CONSTRAINT_NAME = "LineStopUniqueBusinessKeyConstraint".toUpperCase();


    @Override
    public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
        boolean result = false;

        if (skipCount < 0) {
            result = true; // We gracefully skip in case where the caller of this method gives us skipCount<0
        } else if (isExpectedViolation(t)) {
            result = true;
        }

        return result;
    }

    private static boolean isExpectedViolation(Throwable e) {
        boolean result = false;

        if (e instanceof ConstraintViolationException) {
            // We must ignore accent (we use Local.ENGLISH for that) and case
            result = ((ConstraintViolationException) e).getConstraintName().toUpperCase(Locale.ENGLISH).equals(CONSTRAINT_NAME);
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            // We must ignore accent (we use Local.ENGLISH for that) and case
            result = e.getMessage().toUpperCase(Locale.ENGLISH).contains(CONSTRAINT_NAME);
        } else if (e instanceof PersistenceException) {
            /**
             * We are in the case where Hibernate encapsulate the Exception into a PersistenceException.
             * Then we must check recursively into causes.
             */
            if (e.getCause() != null) {
                result = isExpectedViolation(e.getCause());
            }
        }

        return result;
    }


}
