package org.springframework.batch.item.file;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.batch.item.ParseException;

/**
 * @author Almex
 */
public class RowMappingException extends ParseException {

    private Row row;

    private int lineNumber;

    public RowMappingException(String message, Row row) {
        super(message);
        this.row = row;
    }

    public RowMappingException(String message, Row row, int lineNumber) {
        super(message);
        this.row = row;
        this.lineNumber = lineNumber;
    }

    public RowMappingException(String message, Throwable cause, Row row, int lineNumber) {
        super(message, cause);
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
