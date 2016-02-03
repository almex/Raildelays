package be.raildelays.batch.gtfs;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * {@link FieldSetMapper} capable to map a GTFS routes.txt file into a {@link Route}.
 *
 * @author Almex
 * @since 2.0
 */
public class RoutesFieldSetMapper implements GtfsFiledSetMapper<Route> {

    @Override
    public Route mapFieldSet(FieldSet fieldSet) throws BindException {
        Route result = new Route();

        result.setRouteId(fieldSet.readString("route_id"));
        result.setRouteShortName(fieldSet.readString("route_short_name"));
        result.setRouteLongName(fieldSet.readString("route_long_name"));

        return result;
    }
}
