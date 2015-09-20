package org.springframework.batch.item.file;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Almex
 */
public class RowMappingExceptionTest {

    private Row row;

    @Before
    public void setUp() throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();

        row = workbook.createSheet().createRow(0);
    }

    @Test(expected = RowMappingException.class)
    public void test2ParametersConstructor() {
        throw new RowMappingException("", row);
    }

    @Test(expected = RowMappingException.class)
    public void test3ParametersConstructor() {
        throw new RowMappingException("", row, 0);
    }

    @Test(expected = RowMappingException.class)
    public void test4ParametersConstructor() {
        RowMappingException exception = new RowMappingException("", row, 0, new Exception());

        Assert.assertEquals(0, exception.getLineNumber());
        Assert.assertEquals(0, exception.getRow().getRowNum());

        throw exception;
    }
}