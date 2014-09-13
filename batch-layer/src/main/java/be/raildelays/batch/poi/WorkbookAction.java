package be.raildelays.batch.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * In order to deal with the two format of an Excel File (e.g: OLE2 and OXML),
 * this class allow to define what do to when you have a {@link org.apache.poi.hssf.usermodel.HSSFWorkbook}
 * and what to do when you have a {@link org.apache.poi.xssf.usermodel.XSSFWorkbook}.
 *
 * @author Almex
 * @since 1.1
 */
public abstract class WorkbookAction<T> {
    protected Workbook internalWorkbook;

    public WorkbookAction(Workbook workbook) {
        this.internalWorkbook = workbook;
    }

    protected abstract T doWithHSSFWorkbook(HSSFWorkbook workbook);

    protected abstract T doWithXSSFWorkbook(XSSFWorkbook workbook);

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
