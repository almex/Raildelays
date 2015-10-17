package be.raildelays.batch.reader;

import be.raildelays.domain.entities.Train;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * @author Almex
 * @since 2.0
 */
public class TrainFieldSetMapper implements FieldSetMapper<Train> {
    @Override
    public Train mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Train(fieldSet.readRawString("route_short_name"));
    }
}
