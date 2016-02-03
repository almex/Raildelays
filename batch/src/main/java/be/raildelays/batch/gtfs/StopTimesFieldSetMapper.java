package be.raildelays.batch.gtfs;

import be.raildelays.domain.entities.LineStop;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * {@link FieldSetMapper} capable to map a GTFS stop_times.txt file into a {@link LineStop}.
 *
 * @author Almex
 * @since 2.0
 */
public class StopTimesFieldSetMapper implements GtfsFiledSetMapper<StopTime> {

    @Override
    public StopTime mapFieldSet(FieldSet fieldSet) throws BindException {
        StopTime result = new StopTime();

        result.setTripId(fieldSet.readString("trip_id"));
        result.setArrivalTime(GtfsFiledSetMapper.readTime(fieldSet, "arrival_time"));
        result.setDepartureTime(GtfsFiledSetMapper.readTime(fieldSet, "departure_time"));
        result.setStopId(fieldSet.readString("stop_id"));
        result.setStopSequence(fieldSet.readInt("stop_sequence"));

        return result;
    }
}
