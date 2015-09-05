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
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.file.ResourceAwareItemWriterItemStream;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Almex
 * @since 1.2
 */
public class ResourceLocatorExcelRowItemStreamWriterAdaptor implements ItemStreamWriter<ExcelRow> {

    private ResourceAwareItemWriterItemStream<? super ExcelRow> delegate;
    private String fileName;
    private String fileExtension;
    private String directoryPath;
    private boolean initialized = false;
    private ExecutionContext executionContext;

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.executionContext = executionContext;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
        initialized = false;
    }

    @Override
    public void write(List<? extends ExcelRow> items) throws Exception {
        if (!initialized && items.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String suffix = format.format(items.get(0).getDate());
            File file = ExcelFileUtils.getFile(new File(directoryPath), fileName, suffix, fileExtension);

            delegate.setResource(new FileSystemResource(file));
            delegate.open(executionContext);
            initialized = true;
        }

        delegate.write(items);
    }

    public void setDelegate(ResourceAwareItemWriterItemStream<? super ExcelRow> delegate) {
        this.delegate = delegate;
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
