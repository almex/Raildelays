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

package be.raildelays.domain.entities;

import javax.persistence.*;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Set;

/**
 * <p>
 * Abstract entity parent of all entities of this project.
 * In our JPA data model we don't use the Pessimistic Locking (see {@code @Version} JPA annotation). Otherwise
 * Spring Data JPA will assume that an entity is new by testing if the version is {@code null} or not
 * (see {@code org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation#isNew(java.lang.Object)}).
 * </p>
 * <p>
 * Knowing that we only have one connection targeting the same database, we do not need such transactional mechanism.
 * </p>
 * <p>
 * Note that all our entities use the Value Object Design Pattern and therefor we do not want to copy {@code version}
 * and {@code id} from one instance to another instance of the same Entity. Only if the {@code id} is not {@code null}
 * should mark the Entity as new or not (see Spring Data JPA default behaviour in
 * {@code org.springframework.data.repository.core.support.AbstractEntityInformation#isNew(java.lang.Object)}).
 * </p>
 *
 * @author Almex
 * @since 1.0
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    public Long getId() {
        return id;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    /**
     * Use Bean Validation to validate an entity.
     *
     * @param object to validate
     * @param <T> type of the Object
     * @return the Object itself
     * @throws IllegalArgumentException
     */
    protected static <T> T validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for (ConstraintViolation violation : violations) {
                builder.append("\n");
                builder.append(violation.getPropertyPath().toString());
                builder.append(' ');
                builder.append(violation.getMessage());
            }

            throw new IllegalArgumentException(builder.toString());
        }

        return object;
    }
}
