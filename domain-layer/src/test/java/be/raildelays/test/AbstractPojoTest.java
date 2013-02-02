package be.raildelays.test;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.junit.Before;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <b>How to use it</b>:<br/>
 * Just extend this abstract class and add some class variable of type of the
 * class that you want to test. Annotate these variables with
 * <code>@Datapoint</code>
 * </p>
 * <p>
 * <i>It's advised to only log tests that respect assumptions.</i>
 * </p>
 * 
 * @author Alexis SOUMAGNE.
 * 
 * @see DataPoint
 * @see Theories
 */
@RunWith(Theories.class)
public abstract class AbstractPojoTest {

    /**
     * Null reference of a generic type
     */
    @DataPoint
    public static final Object NULL_OBJECT = null;
    /**
     * Reference to a generic type
     */
    @DataPoint
    public static final Object OBJECT = new Object();

    /**
     * Logger for this class.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPojoTest.class);

    /**
     * Use reflection trick to use the <code>setId(T id)</code> method that is
     * private in your entity.
     * 
     * @param <K> type of the id
     * @param entity to modify
     * @param id to set
     * @return the <code>obj</code> parameter
     * @throws IllegalArgumentException if the method <code>setId(T id)</code>
     *             does not exists or is not accessible
     * @throws RuntimeException for unexpected errors
     */
    protected static <K> Object setIdFor(final Object entity, final K id) {
        try {
            final Method method = entity.getClass()
                .getDeclaredMethod("setId",
                            new Class[] { id.getClass() });

            // doPrivileged block
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    method.setAccessible(true);
                    return null;
                }
            });
            method.invoke(entity, new Object[] { id });

            return entity;
        } catch (RuntimeException e) {
            LOGGER.error("Unexpected error!", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Method setId(T id) does not exists.", e);
            throw new IllegalArgumentException("Method setId(T id) does not"
                    + " exists.",
                e);
        }
    }
    
    /**
     * Override this method to provide initialize your data points.
     */
    @Before
    public abstract void setUp() throws Exception;

}
