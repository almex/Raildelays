package be.raildelays.batch.gtfs;

import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.time.LocalDate;

import static be.raildelays.batch.gtfs.CalendarDate.ExceptionType.ADDED;
import static be.raildelays.batch.gtfs.CalendarDate.ExceptionType.REMOVED;

/**
 * @author Almex
 */
public class CalendarDateTest extends AbstractObjectTest {

    @DataPoint
    public static CalendarDate DATA_POINT1;
    @DataPoint
    public static CalendarDate DATA_POINT2;
    @DataPoint
    public static CalendarDate DATA_POINT3;
    @DataPoint
    public static CalendarDate DATA_POINT4;

    @Before
    public void setUp() throws Exception {
        DATA_POINT1 = new CalendarDate();
        DATA_POINT1.setDate(LocalDate.now());
        DATA_POINT1.setExceptionType(REMOVED);
        DATA_POINT1.setServiceId("foo");

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new CalendarDate();
        DATA_POINT3.setDate(DATA_POINT1.getDate());
        DATA_POINT3.setExceptionType(DATA_POINT1.isIncluded(DATA_POINT1.getDate()) ? ADDED : REMOVED);
        DATA_POINT3.setServiceId(DATA_POINT1.getServiceId());

        DATA_POINT4 = new CalendarDate();
        DATA_POINT3.setDate(LocalDate.now());
        DATA_POINT3.setExceptionType(CalendarDate.ExceptionType.valueOfIndex(1));
        DATA_POINT3.setServiceId("bar");
    }
}