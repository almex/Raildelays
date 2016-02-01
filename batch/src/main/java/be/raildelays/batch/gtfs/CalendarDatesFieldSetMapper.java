package be.raildelays.batch.gtfs;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * {@link FieldSetMapper} capable to map a GTFS calendar_dates.txt file into a {@link CalendarDate}.
 *
 * @author Almex
 * @since 2.0
 */
public class CalendarDatesFieldSetMapper implements GtfsFiledSetMapper<CalendarDate> {

    @Override
    public CalendarDate mapFieldSet(FieldSet fieldSet) throws BindException {
        CalendarDate result = new CalendarDate();

        result.setDate(GtfsFiledSetMapper.readDate(fieldSet, "date"));
        result.setServiceId(fieldSet.readRawString("service_id"));
        result.setExceptionType(CalendarDate.ExceptionType.valueOfIndex(fieldSet.readInt("exception_type")));

        return result;
    }
}
