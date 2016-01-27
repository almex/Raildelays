package be.raildelays.domain.entities;

import be.raildelays.domain.Language;
import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Almex
 */
public class I18NEntityTest extends AbstractObjectTest {

    @DataPoint
    public static I18nEntity DATA_POINT1;
    @DataPoint
    public static I18nEntity DATA_POINT2;
    @DataPoint
    public static I18nEntity DATA_POINT3;
    @DataPoint
    public static I18nEntity DATA_POINT4;

    @Before
    public void setUp() {
        DATA_POINT1 = new I18nEntity("foo", Language.EN);
        DATA_POINT2 = new I18nEntity("foo", null);
        DATA_POINT3 = new I18nEntity("foo", Language.FR);
        DATA_POINT4 = new I18nEntity("bar", Language.NL);
    }

    @Test
    public void testGetNotNullName() throws Exception {
        assertNotNull(I18nEntity.getNotNullName(DATA_POINT1));
        assertNotNull(I18nEntity.getNotNullName(DATA_POINT2));
        assertNotNull(I18nEntity.getNotNullName(DATA_POINT3));
        assertNotNull(I18nEntity.getNotNullName(DATA_POINT4));
    }

    @Test
    public void testAccessors() {
        assertNotNull(DATA_POINT1.getName(Language.EN));
        assertNull(DATA_POINT2.getName(Language.NL));
        assertNotNull(DATA_POINT3.getName(Language.FR));
        assertNotNull(DATA_POINT4.getName(Language.NL));
    }
}