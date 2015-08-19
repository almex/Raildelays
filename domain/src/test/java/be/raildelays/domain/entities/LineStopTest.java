package be.raildelays.domain.entities;

import be.raildelays.delays.TimestampDelay;
import be.raildelays.test.AbstractObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LineStopTest extends AbstractObjectTest {

    @DataPoint
    public static LineStop DATA_POINT1;

    @DataPoint
    public static LineStop DATA_POINT2;

    @DataPoint
    public static LineStop DATA_POINT3;

    @DataPoint
    public static LineStop DATA_POINT4;

    @Override
    @Before
    public void setUp() throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("HH:mm");
        Train train = new Train("466");
        Station station = new Station("Liège (Liège-Guillemins)");
        TimestampDelay arrivalTime = TimestampDelay.of(f.parse("12:00"), 5L);
        TimestampDelay departureTime = TimestampDelay.of(f.parse("12:05"), 5L);
        Date date = new Date();

        DATA_POINT1 = new LineStop.Builder().date(date).train(train)
                .station(station).arrivalTime(arrivalTime)
                .departureTime(departureTime).canceled(false).build();

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new LineStop.Builder().date(date).train(train)
                .station(station).arrivalTime(arrivalTime)
                .departureTime(departureTime).canceled(false).build();

        DATA_POINT4 = new LineStop.Builder().date(new Date())
                .train(new Train("469"))
                .station(new Station("Brussels (Bruxelles-central)"))
                .arrivalTime(arrivalTime).departureTime(departureTime)
                .canceled(false).build();
    }

}
