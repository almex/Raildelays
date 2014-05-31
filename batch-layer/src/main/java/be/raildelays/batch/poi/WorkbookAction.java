package be.raildelays.batch.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
* Created by soumagn on 31/05/2014.
*/
public abstract class WorkbookAction<T> {
    protected abstract T doWithHSSFWorkbook(HSSFWorkbook workbook);

    protected abstract T doWithXSSFWorkbook(XSSFWorkbook workbook);

    protected Workbook internalWorkbook;

    public WorkbookAction(Workbook workbook) {
        this.internalWorkbook = workbook;
    }

    public T execute() throws InvalidFormatException {
        if (internalWorkbook instanceof HSSFWorkbook) {
            return doWithHSSFWorkbook((HSSFWorkbook) internalWorkbook);
        } else if (internalWorkbook instanceof XSSFWorkbook) {
            return doWithXSSFWorkbook((XSSFWorkbook) internalWorkbook);
        } else {
            throw new InvalidFormatException("Format not supported!");
        }
    }
}
