package be.raildelays.domain.entities;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.experimental.theories.DataPoint;

public class TrainLineTest extends AbstractObjectTest {

    @DataPoint
    public static TrainLine DATA_POINT1;

    @DataPoint
    public static TrainLine DATA_POINT2;

    @DataPoint
    public static TrainLine DATA_POINT3;

    @DataPoint
    public static TrainLine DATA_POINT4;

    @DataPoint
    public static TrainLine DATA_POINT5;

    @DataPoint
    public static TrainLine DATA_POINT6;

    /**
     * Override this method to provide initialize your data points.
     */
    @Override
    public void setUp() throws Exception {
        DATA_POINT1 = new TrainLine.Builder(466L).build();

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new TrainLine.Builder(466L).build();

        DATA_POINT4 = new TrainLine.Builder(1717L).build();

        DATA_POINT5 = new TrainLine.Builder((Long) null).build(false);

    }
}
