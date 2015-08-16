package org.springframework.batch.item.file;

import org.apache.poi.ss.usermodel.Row;

/**
 * This interface allow you to provide a valid implementation to map an Excel {@link org.apache.poi.ss.usermodel.Row}
 * to whatever type you want.
 *
 * @param <T> return type of a {@link org.springframework.batch.item.ItemReader#read()}
 * @author Almex
 * @see org.springframework.batch.item.file.ExcelSheetItemReader
 * @since 1.1
 */
public interface RowMapper<T> {

    /**
     * Map a row into an object of type <code>T</code>.
     *
     * @param row      current Excel row to map into an object of type <code>T</code>.
     * @param rowIndex index returned by the method
     *                 {@link IndexedResourceAwareItemStreamReader#getCurrentIndex()}.
     *                 Meaning that the difference between {@link org.apache.poi.ss.usermodel.Row#getRowNum()} and this
     *                 value depends on value of {@link org.springframework.batch.item.file.ExcelSheetItemReader#setRowsToSkip(int)}.
     * @return your object or null if you consider that the input row is the EOF.
     * @throws RowMappingException in case of any error of mapping.
     */
    T mapRow(Row row, int rowIndex) throws RowMappingException;
}
