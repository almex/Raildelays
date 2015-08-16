package be.raildelays.test;// NOPMD TooManyStaticImports
//justification: in this case it's more readable like this

import org.junit.experimental.theories.Theory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

/**
 * <p>
 * This is a generic unit testing for methods <code>equals()</code>,
 * <code>toString()</code> and <code>hashCode()</code>. These will generate some
 * code to respect contract specification coming from Javadoc of the
 * {@link java.lang.Object} class .
 * </p>
 * <p>
 * </p>
 * <br/>
 * <p>
 * <b>How to use it</b>:<br/>
 * Just extend this abstract class and add some class variable of type of the
 * class that you want to test. Annotate these variables with
 * <code>@Datapoint</code>
 * </p>
 * <p>
 * <i>We log only tests that respect assumptions.</i>
 * </p>
 *
 * @author Alexis SOUMAGNE.
 * @see AbstractPojoTest
 * @see Object
 */
public abstract class AbstractObjectTest extends AbstractPojoTest {

    /**
     * Logger for this class.
     */
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractObjectTest.class);

    /**
     * How many times we loop to test that hashCode() and equals() are self
     * consistent
     */
    protected static final int NB_CONSISTENT_LOOP = 10;

    /**
     * Just execute toString() method to see if there is no exception thrown. If
     * this method failed all others tests will also failed because we log
     * parameter value using toString() method.
     *
     * @param x object to test
     * @see java.lang.Object#toString()
     */
    @Theory
    public void theoryToStringIsFunctional(final Object x) {
        // No log to see x value because it use toString() method
        assumeThat(x, is(notNullValue()));

        assertThat(x.toString() != null, is(true));
    }

    /**
     * It is reflexive: for any non-null reference value x, x.equals(x) should
     * return true.
     *
     * @param x object to test
     */
    @Theory
    public void theoryEqualsIsReflexive(final Object x) {
        assumeThat(x, is(notNullValue()));

        LOGGER.trace("testEqualsIsReflexive({})", x);
        assertThat(x.equals(x), is(true));
    }

    /**
     * It is symmetric: for any non-null reference values x and y, x.equals(y)
     * should return true if and only if y.equals(x) returns true.
     *
     * @param x first parameter
     * @param y second parameter
     */
    @Theory
    public void theoryEqualsIsSymmetric(final Object x, final Object y) {
        assumeThat(x, is(notNullValue()));
        assumeThat(y, is(notNullValue()));
        assumeThat(y.equals(x), is(not(true)));

        LOGGER.trace("testEqualsIsSymmetric({}, {})", x, y);
        assertThat(x.equals(y), is(false));
    }

    /**
     * It is transitive: for any non-null reference values x, y, and z, if
     * x.equals(y) returns true and y.equals(z) returns true, then x.equals(z)
     * should return true.
     *
     * @param x first parameter
     * @param y second parameter
     * @param z third parameter
     */
    @Theory
    public void theoryEqualsIsTransitive(final Object x, final Object y,
                                         final Object z) {
        assumeThat(x, is(notNullValue()));
        assumeThat(y, is(notNullValue()));
        assumeThat(z, is(notNullValue()));
        assumeThat(x.equals(y), is(true));
        assumeThat(y.equals(z), is(true));

        LOGGER.trace("testEqualsIsTransitive({}, {}, {})", new Object[]{x, y,
                z});
        assertThat(z.equals(x), is(true));
    }

    /**
     * It is consistent: for any non-null reference values x and y, multiple
     * invocations of x.equals(y) consistently return true or consistently
     * return false, provided no information used in equals comparisons on the
     * objects is modified.
     *
     * @param x first parameter
     * @param y second parameter
     */
    @Theory
    public void theoryEqualsIsConsistent(final Object x, final Object y) {
        assumeThat(x, is(notNullValue()));

        LOGGER.trace("testEqualsIsConsistent({}, {})", x, y);
        final boolean theSame = x.equals(y);

        for (int i = 0; i < NB_CONSISTENT_LOOP; i++) {
            assertThat(x.equals(y), is(equalTo(theSame)));
        }
    }

    /**
     * For any non-null reference value x, x.equals(null) should return false.
     *
     * @param x object to test
     * @param y object to test
     */
    @Theory
    public void theoryEqualsReturnFalseWithNull(final Object x, final Object y) {
        assumeThat(x, is(notNullValue()));
        assumeThat(y, is(nullValue()));

        LOGGER.trace("testEqualsReturnFalseWithNull({})", x);
        assertThat(x.equals(y), is(false));
    }

    /**
     * Whenever it is invoked on the same object more than once during an
     * execution of a Java application, the hashCode method must consistently
     * return the same integer, provided no information used in equals
     * comparisons on the object is modified. This integer need not remain
     * consistent from one execution of an application to another execution of
     * the same application.
     *
     * @param x object to test
     */
    @Theory
    public void theoryHashCodeIsSelfConsistent(final Object x) {
        assumeThat(x, is(notNullValue()));

        LOGGER.trace("testHashCodeIsSelfConsistent({})", x);
        final int theSame = x.hashCode();

        for (int i = 0; i < NB_CONSISTENT_LOOP; i++) {
            assertThat(x.hashCode(), is(equalTo(theSame)));
        }
    }

    /**
     * If two objects are equal according to the equals(Object) method, then
     * calling the hashCode method on each of the two objects must produce the
     * same integer result.
     *
     * @param x first parameter
     * @param y second parameter
     */
    @Theory
    public void theoryHashCodeIsConsistentWithEquals(final Object x,
                                                     final Object y) {
        assumeThat(x, is(notNullValue()));
        assumeThat(x.equals(y), is(true));

        LOGGER.trace("testHashCodeIsConsistentWithEquals({}, {})", x, y);
        assertThat(x.hashCode(), is(equalTo(y.hashCode())));
    }

    /**
     * Test that equals doesn't work if x and y are from different type.
     *
     * @param x first parameter
     * @param y second parameter
     */
    @Theory
    public void theoryDifferentTypeAreNotEquals(final Object x, final Object y) {
        assumeThat(x, is(notNullValue()));
        assumeThat(y, is(notNullValue()));
        assumeThat(x.getClass().isAssignableFrom(y.getClass()), is(false));

        LOGGER.trace("testDifferentTypeAreNotEquals({}, {})", x.getClass(),
                y.getClass());
        assertThat(x.equals(y), is(false));
    }

}
