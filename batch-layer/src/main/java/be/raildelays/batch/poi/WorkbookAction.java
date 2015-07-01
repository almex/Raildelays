/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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
