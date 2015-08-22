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

package be.raildelays.logging;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.railtime.TwoDirections;
import be.raildelays.domain.xls.ExcelRow;

import java.util.List;

/**
 * Strongly typed logger interface.
 *
 * @author Almex
 */
public interface Logger extends org.slf4j.Logger {

    void info(String message, LineStop lineStop);

    void debug(String message, LineStop lineStop);

    void trace(String message, LineStop lineStop);

    void info(String message, List<LineStop> lineStops);

    void debug(String message, List<LineStop> lineStops);

    void trace(String message, List<LineStop> lineStops);

    void info(String message, ExcelRow excelRow);

    void debug(String message, ExcelRow excelRow);

    void trace(String message, ExcelRow excelRow);

    void info(String message, BatchExcelRow excelRow);

    void debug(String message, BatchExcelRow excelRow);

    void trace(String message, BatchExcelRow excelRow);

    void info(String message, RouteLogDTO routeLog);

    void debug(String message, RouteLogDTO routeLog);

    void trace(String message, RouteLogDTO routeLog);

    void info(String message, TwoDirections twoDirections);

    void debug(String message, TwoDirections twoDirections);

    void trace(String message, TwoDirections twoDirections);

}
