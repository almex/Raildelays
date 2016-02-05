package be.raildelays.batch.gtfs;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Interface containing all common static methods between all {@link FieldSetMapper}
 * in reading GTFS data files.
 *
 * @author Almex
 * @since 2.0
 */
public interface GtfsFiledSetMapper<T> extends FieldSetMapper<T> {

    String TIME_FORMAT = "HH:mm:ss";
    String DATE_FORMAT = "yyyyMMdd";

    static Long parseRouteId(String shortName) {
        StringBuilder builder = new StringBuilder();
        Pattern pattern = Pattern.compile("[0-9]+");
        Long result = null;

        if (shortName != null) {
            // Retrieved all numeric values
            for (int i = 0; i < shortName.length(); i++) {
                char character = shortName.charAt(i);

                if (pattern.matcher(new String(new char[]{character})).find()) {
                    builder.append(character);
                }
            }

            result = Long.parseLong(builder.toString());
        }

        return result;
    }

    static LocalTime readTime(FieldSet fieldSet, String name) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT, Locale.ENGLISH)
                .withChronology(IsoChronology.INSTANCE)
                .withResolverStyle(ResolverStyle.LENIENT); // We should be able to parse 25:00:01
        String value = fieldSet.readString(name);

        return LocalTime.parse(value, formatter);
    }

    static LocalDate readDate(FieldSet fieldSet, String name) {
        return fieldSet.readDate(name, DATE_FORMAT)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
