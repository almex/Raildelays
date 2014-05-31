package be.raildelays.batch.poi;

import org.apache.poi.ss.usermodel.Row;

/**
 * @author Almex
 */
public interface RowMapper<T> {
    T mapRow(Row row, int rowIndex) throws Exception;
}
