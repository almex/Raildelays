package be.raildelays.batch.skip;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.PersistenceException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Almex
 */
public class SkipUniqueKeyViolationPolicyTest {

    private SkipUniqueKeyViolationPolicy skipPolicy;

    @Before
    public void setUp() throws Exception {
        skipPolicy = new SkipUniqueKeyViolationPolicy();
    }

    @Test
    public void testSkipConstraintViolationException() throws Exception {
        Assert.assertTrue(skipPolicy.shouldSkip(new ConstraintViolationException("foo",
                                new SQLException(),
                                SkipUniqueKeyViolationPolicy.CONSTRAINT_NAME
                        ),
                        0
                )
        );
    }

    @Test
    public void testSkipSQLIntegrityConstraintViolationException() throws Exception {
        Assert.assertTrue(skipPolicy.shouldSkip(new SQLIntegrityConstraintViolationException(SkipUniqueKeyViolationPolicy.CONSTRAINT_NAME), 0));
    }

    @Test
    public void testSkipPersistenceExceptionWithSQLIntegrityConstraintViolationException() throws Exception {
        Assert.assertTrue(skipPolicy.shouldSkip(new PersistenceException("foo",
                                new SQLIntegrityConstraintViolationException(SkipUniqueKeyViolationPolicy.CONSTRAINT_NAME)
                        ),
                        0
                )
        );
    }

    @Test
    public void testSkipPersistenceExceptionWithConstraintViolationException() throws Exception {
        Assert.assertTrue(skipPolicy.shouldSkip(new PersistenceException("",
                                new ConstraintViolationException("foo",
                                        new SQLException(),
                                        SkipUniqueKeyViolationPolicy.CONSTRAINT_NAME)
                        ),
                        0
                )
        );
    }

    @Test
    public void testNotSkipException() throws Exception {
        Assert.assertFalse(skipPolicy.shouldSkip(new Exception(), 0));
    }

    @Test
    public void testGracefulSkip() throws Exception {
        Assert.assertTrue(skipPolicy.shouldSkip(null, -1));
    }


}