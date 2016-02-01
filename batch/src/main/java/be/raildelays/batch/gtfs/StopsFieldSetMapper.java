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
public class StopsFieldSetMapper implements GtfsFiledSetMapper<Stop> {

    @Override
    public Stop mapFieldSet(FieldSet fieldSet) throws BindException {
        Stop result = new Stop();

        result.setStopId(fieldSet.readRawString("stop_id"));
        result.setStopName(fieldSet.readRawString("stop_name"));
        result.setStopLat(fieldSet.readRawString("stop_lat"));
        result.setStopLon(fieldSet.readRawString("stop_lon"));
        result.setStopCode(fieldSet.readRawString("platform_code"));
        result.setLocationType(Stop.LocationType.valueOfIndex(fieldSet.readInt("location_type")));
        result.setParentStation(fieldSet.readRawString("parent_station"));

        return result;
    }
}
