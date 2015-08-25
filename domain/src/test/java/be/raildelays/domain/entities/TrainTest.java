package be.raildelays.domain.entities;

import be.raildelays.domain.Language;
import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.experimental.theories.DataPoint;

public class TrainTest extends AbstractObjectTest {

    @DataPoint
    public static Train DATA_POINT1;

    @DataPoint
    public static Train DATA_POINT2;

    @DataPoint
    public static Train DATA_POINT3;

    @DataPoint
    public static Train DATA_POINT4;

    @DataPoint
    public static Train DATA_POINT5;

    @DataPoint
    public static Train DATA_POINT6;

    /**
     * Override this method to provide initialize your data points.
     */
    @Override
    public void setUp() throws Exception {
        DATA_POINT1 = new Train("466");

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new Train("466", "466", "466");

        DATA_POINT4 = new Train("1717", Language.FR);

        DATA_POINT4 = new Train("1717", Language.EN);

        DATA_POINT4 = new Train("1717", Language.NL);

        //DATA_POINT4 = new Train("1717", null); this must throw a NPE

        DATA_POINT4 = new Train(null, Language.NL);

    }
}
