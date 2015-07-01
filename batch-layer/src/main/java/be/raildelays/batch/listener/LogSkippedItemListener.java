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

package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.xls.ExcelRow;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.SkipListener;

/**
 * This class logs skipped item via Slf4j.
 *
 * @author Almex
 */
public class LogSkippedItemListener implements SkipListener<Object, BatchExcelRow>, ItemProcessListener<Object, Object> {

    private static final Logger READ = LoggerFactory.getLogger("RD", LogSkippedItemListener.class);

    private static final Logger PROCESS = LoggerFactory.getLogger("PR", LogSkippedItemListener.class);

    private static final Logger WRITE = LoggerFactory.getLogger("WR", LogSkippedItemListener.class);


    @Override
    public void onSkipInRead(Throwable t) {
        READ.debug(t.getMessage(), (ExcelRow) null);
    }

    @Override
    public void onSkipInWrite(BatchExcelRow item, Throwable t) {
        WRITE.info("on_skip_write", item);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        if (item instanceof LineStop) {
            PROCESS.info(t.getMessage(), (LineStop) item);
        } else if (item instanceof ExcelRow) {
            PROCESS.info(t.getMessage(), (ExcelRow) item);
        } else {
            PROCESS.info("unknown_type", (ExcelRow) null);
            PROCESS.debug(t.getMessage(), (ExcelRow) null);
        }
    }

    @Override
    public void beforeProcess(Object item) {
    }

    @Override
    public void afterProcess(Object item, Object result) {
        if (item != null && result == null) {
            if (item instanceof LineStop) {
                PROCESS.info("filtering", (LineStop) item);
            } else if (item instanceof ExcelRow) {
                PROCESS.info("filtering", (ExcelRow) item);
            } else {
                PROCESS.info("filtering_unknown", (ExcelRow) null);
            }
        }
    }

    @Override
    public void onProcessError(Object item, Exception e) {
    }
}
