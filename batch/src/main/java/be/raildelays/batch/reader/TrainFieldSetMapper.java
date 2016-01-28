package be.raildelays.batch.reader;

import be.raildelays.domain.entities.TrainLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * {@link FieldSetMapper} capable to map a Route under its GTFS shape into a {@link TrainLine}.
 *
 * @author Almex
 * @since 2.0
 */
public class TrainFieldSetMapper implements RaildelaysFiledSetMapper<TrainLine> {

    @Override
    public TrainLine mapFieldSet(FieldSet fieldSet) throws BindException {
        Long routeId = RaildelaysFiledSetMapper.parseRouteId(fieldSet.readRawString("route_id"));
        String shortName = fieldSet.readRawString("route_short_name");
        String longName = fieldSet.readRawString("route_long_name");

        return new TrainLine.Builder(routeId)
                .shortName(shortName)
                .longName(longName)
                .build();
    }
}
