package org.springframework.batch.item.file;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.item.ParseException;

/**
 * Exception thrown when a mapping failed during a read.
 *
 * @author Almex
 * @since 1.2
 * @see RowMapper
 */
public class RowMappingException extends ParseException {

    private final Row row;

    private final int lineNumber;

    public RowMappingException(Exception e, Row row) {
        super("Mapping error: '" + e.getMessage() + " at rowNum=" + (row != null ? row.getRowNum() : "null"));
        this.row = row;
        this.lineNumber = 0;
    }

    public RowMappingException(Exception e, Row row, int lineNumber) {
        super("Mapping error: '" + e.getMessage() + "' at lineNumber=" + lineNumber + ", rowNum=" + (row != null ? row.getRowNum() : "null"));
        this.row = row;
        this.lineNumber = lineNumber;
    }

    public RowMappingException(Exception e, Row row, int lineNumber, Throwable cause) {
        super("Mapping error: '" + e.getMessage() + "' at lineNumber=" + lineNumber + ", rowNum=" + (row != null ? row.getRowNum() : "null"), cause);
        this.row = row;
        this.lineNumber = lineNumber;
    }

    public Row getRow() {
        return row;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
