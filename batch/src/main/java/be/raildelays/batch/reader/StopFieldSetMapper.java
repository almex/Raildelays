package be.raildelays.batch.reader;

import be.raildelays.delays.TimeDelay;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.TrainLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalTime;
import java.time.ZoneId;

/**
 * {@link FieldSetMapper} capable to map a Stop under its GTFS shape into a {@link LineStop}.
 *
 * @author Almex
 * @since 2.0
 */
public class StopFieldSetMapper implements FieldSetMapper<LineStop> {

    public static final String TIME_FORMAT = "HH:mm:ss";

    @Override
    public LineStop mapFieldSet(FieldSet fieldSet) throws BindException {
        Long routeId = RaildelaysFiledSetMapper.parseRouteId(fieldSet.readRawString("trip_id"));
        LocalTime arrivalTime = fieldSet.readDate("arrival_time", TIME_FORMAT).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime departureTime = fieldSet.readDate("departure_time", TIME_FORMAT).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        return new LineStop.Builder()
                .arrivalTime(TimeDelay.of(arrivalTime))
                .departureTime(TimeDelay.of(departureTime))
                .trainLine(new TrainLine.Builder(routeId).build())
                .build(false);
    }
}
