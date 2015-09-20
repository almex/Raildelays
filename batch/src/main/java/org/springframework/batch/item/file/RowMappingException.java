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

    private Row row;

    private int lineNumber;

    public RowMappingException(String message, Row row) {
        super("Mapping error: '" + message + " at rowNum=" + (row != null ? row.getRowNum() : "null"));
        this.row = row;
    }

    public RowMappingException(String message, Row row, int lineNumber) {
        super("Mapping error: '" + message + "' at lineNumber=" + lineNumber + ", rowNum=" + (row != null ? row.getRowNum() : "null"));
        this.row = row;
        this.lineNumber = lineNumber;
    }

    public RowMappingException(String message, Row row, int lineNumber, Throwable cause) {
        super("Mapping error: '" + message + "' at lineNumber=" + lineNumber + ", rowNum=" + (row != null ? row.getRowNum() : "null"), cause);
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
