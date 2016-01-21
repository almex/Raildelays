package be.raildelays.batch.reader;

import be.raildelays.domain.entities.TrainLine;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.regex.Pattern;

/**
 * {@link FieldSetMapper} capable to map a Route under its GTFS form into a {@link TrainLine}.
 *
 * @author Almex
 * @since 2.0
 */
public class TrainFieldSetMapper implements FieldSetMapper<TrainLine> {

    @Override
    public TrainLine mapFieldSet(FieldSet fieldSet) throws BindException {
        Long routeId = parseRouteId(fieldSet.readRawString("route_id"));
        String shortName = fieldSet.readRawString("route_short_name");
        String longName = fieldSet.readRawString("route_long_name");

        return new TrainLine.Builder(routeId)
                .shortName(shortName)
                .longName(longName)
                .build();
    }

    private Long parseRouteId(String shortName) {
        StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile("[0-9]+");

        // Retrieved all numeric values
        for (int i = 0; i < shortName.length(); i++) {
            char character = shortName.charAt(i);

            if (pattern.matcher(new String(new char[]{character})).find()) {
                builder.append(character);
            }
        }

        return Long.parseLong(builder.toString());
    }
}
