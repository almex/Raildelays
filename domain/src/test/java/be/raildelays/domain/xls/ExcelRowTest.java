package be.raildelays.domain.xls;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TrainLine;
import com.github.almex.pojounit.AbstractObjectTest;
import org.junit.experimental.theories.DataPoint;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExcelRowTest extends AbstractObjectTest {


    @DataPoint
    public static ExcelRow DATA_POINT1;

    @DataPoint
    public static ExcelRow DATA_POINT2;

    @DataPoint
    public static ExcelRow DATA_POINT3;

    @DataPoint
    public static ExcelRow DATA_POINT4;

    @Override
    public void setUp() throws Exception {
        DATA_POINT1 = new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .departureStation(new Station("Liège-Guillemins"))
                .arrivalStation(new Station("Bruxelles-Central"))
                .expectedDepartureTime(LocalTime.of(8, 10))
                .expectedArrivalTime(LocalTime.of(9, 10))
                .effectiveDepartureTime(LocalTime.of(8, 10))
                .effectiveArrivalTime(LocalTime.of(9, 30))
                .expectedTrain1(new TrainLine.Builder(466L).build())
                .effectiveTrain1(new TrainLine.Builder(466L).build())
                .build();
        DATA_POINT2 = DATA_POINT1;
        DATA_POINT3 = new ExcelRow.Builder(LocalDate.now(), Sens.DEPARTURE)
                .departureStation(new Station("Liège-Guillemins"))
                .arrivalStation(new Station("Bruxelles-Central"))
                .expectedDepartureTime(LocalTime.of(8, 10))
                .expectedArrivalTime(LocalTime.of(9, 10))
                .effectiveDepartureTime(LocalTime.of(8, 10))
                .effectiveArrivalTime(LocalTime.of(9, 30))
                .expectedTrain1(new TrainLine.Builder(466L).build())
                .effectiveTrain1(new TrainLine.Builder(466L).build())
                .build();
        DATA_POINT4 = new ExcelRow.Builder(LocalDate.now(), Sens.ARRIVAL)
                .departureStation(new Station("Bruxelles-Central"))
                .arrivalStation(new Station("Liège-Guillemins"))
                .expectedDepartureTime(LocalTime.of(15, 10))
                .expectedArrivalTime(LocalTime.of(16, 10))
                .effectiveDepartureTime(LocalTime.of(15, 10))
                .effectiveArrivalTime(LocalTime.of(16, 30))
                .expectedTrain1(new TrainLine.Builder(515L).build())
                .effectiveTrain1(new TrainLine.Builder(515L).build())
                .build();
    }
}
