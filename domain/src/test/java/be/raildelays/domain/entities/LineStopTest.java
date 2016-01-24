package be.raildelays.domain.entities;

import be.raildelays.delays.TimeDelay;
import com.github.almex.pojounit.AbstractCloneableObjectTest;
import org.junit.Before;
import org.junit.experimental.theories.DataPoint;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;

public class LineStopTest extends AbstractCloneableObjectTest {

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
        TrainLine trainLine = new TrainLine.Builder(466L).build();
        Station station = new Station("Liège (Liège-Guillemins)");
        TimeDelay arrivalTime = TimeDelay.of(LocalTime.parse("12:00"), 5L);
        TimeDelay departureTime = TimeDelay.of(LocalTime.parse("12:05"), 5L);
        LocalDate date = LocalDate.now();

        DATA_POINT1 = new LineStop.Builder()
                .date(date)
                .train(trainLine)
                .station(station)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledArrival(false)
                .canceledDeparture(false)
                .build();

        DATA_POINT2 = DATA_POINT1;

        DATA_POINT3 = new LineStop.Builder().date(date).train(trainLine)
                .station(station)
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledArrival(false)
                .canceledDeparture(false)
                .build();

        DATA_POINT4 = new LineStop.Builder().date(LocalDate.now())
                .train(new TrainLine.Builder(469L).build())
                .station(new Station("Brussels (Bruxelles-central)"))
                .arrivalTime(arrivalTime)
                .departureTime(departureTime)
                .canceledArrival(false)
                .canceledDeparture(false)
                .build();
    }

}
