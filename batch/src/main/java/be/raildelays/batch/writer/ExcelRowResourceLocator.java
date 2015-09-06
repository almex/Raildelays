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

package be.raildelays.batch.writer;

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ResourceContext;
import org.springframework.batch.item.file.SimpleResourceLocator;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * We generate the Excel file name based of the first item to write.
 * The {@link ExcelRow#date} is appended to file name prefix, file extension and directory path
 * to obtain the full path of our output file.
 *
 * @author Almex
 * @since 2.0
 */
public class ExcelRowResourceLocator extends SimpleResourceLocator<ExcelRow> {

    private String fileName;
    private String fileExtension;
    private String directoryPath;

    /**
     * {@inheritDoc}
     * <p>
     * We only modify the {@link ResourceContext} when the list of item is not empty and that the
     * {@link ResourceContext} has not been initialized yet.
     * </p>
     */
    @Override
    public void onWrite(List<? extends ExcelRow> items, ResourceContext context) throws ItemStreamException {
        if (items.size() > 0 && !context.hasAlreadyBeenInitialized()) {
            String suffix = items.get(0).getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            File file = ExcelFileUtils.getFile(new File(directoryPath), fileName, suffix, fileExtension);

            context.changeResource(new FileSystemResource(file));
        }
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }
}
