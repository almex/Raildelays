package be.raildelays.batch.reader;

import org.springframework.batch.item.file.mapping.FieldSetMapper;

import java.util.regex.Pattern;

/**
 * Interface containing all common static methods between all {@link FieldSetMapper}.
 *
 * @author Almex
 * @since 2.0
 */
public interface RaildelaysFiledSetMapper<T> extends FieldSetMapper<T> {

    static Long parseRouteId(String shortName) {
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
