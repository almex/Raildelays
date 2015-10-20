package be.raildelays.batch.reader;

import be.raildelays.domain.entities.Train;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.regex.Pattern;

/**
 * @author Almex
 * @since 2.0
 */
public class TrainFieldSetMapper implements FieldSetMapper<Train> {
    @Override
    public Train mapFieldSet(FieldSet fieldSet) throws BindException {
        String shortName = fieldSet.readRawString("route_short_name");
        StringBuffer buffer = new StringBuffer();
        Pattern pattern = Pattern.compile("[0-9]+");

        for (int i = 0; i < shortName.length(); i++) {
            char character = shortName.charAt(i);

            if (pattern.matcher(new String(new char[]{character})).find()) {
                buffer.append(character);
            }
        }

        return new Train(buffer.toString());
    }
}
