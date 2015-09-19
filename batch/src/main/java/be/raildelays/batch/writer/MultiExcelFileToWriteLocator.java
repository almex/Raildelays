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
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.support.ResourceItemSearch;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.file.ExcelSheetItemWriter;
import org.springframework.batch.item.resource.CountingItemResourceLocator;
import org.springframework.batch.item.resource.ResourceContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * @author Almex
 */
public class MultiExcelFileToWriteLocator extends CountingItemResourceLocator<ExcelRow> {

    private Resource directory;
    private String filePrefix;
    private String fileExtension;
    private ResourceItemSearch<BatchExcelRow> resourceItemSearch;
    private boolean forceNewFile = false;

    @Override
    public void onOpen(ResourceContext context) throws ItemStreamException {
        super.onOpen(context);
        if (!forceNewFile) {
            findFirstEmptyRow(context);
        }
    }

    @Override
    public void onWrite(ExcelRow item, ResourceContext context) throws Exception {
        super.onWrite(item, context);

        if (context.getCurrentIndex() >= maxItemCount || !context.containsResource()) {
            String suffix = item.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            File file = ExcelFileUtils.getFile(directory.getFile(), filePrefix, suffix, fileExtension);

            context.changeResource(new FileSystemResource(file));
        }
    }

    private void findFirstEmptyRow(ResourceContext context) throws ItemStreamException {
        try {
            File[] files = directory.getFile().listFiles(pathname ->
                    pathname.getName().endsWith(ExcelSheetItemWriter.Format.OLE2.getFileExtension()) ||
                            pathname.getName().endsWith(ExcelSheetItemWriter.Format.OOXML.getFileExtension()));

            if (files != null) {
                for (File file : files) {
                    //-- We search the first empty Row
                    int index = resourceItemSearch.indexOf(BatchExcelRow.EMPTY, new FileSystemResource(file));

                    if (index != ResourceItemSearch.EOF) {
                        context.changeResource(new FileSystemResource(file));
                        context.setCurrentIndex(index);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ItemStreamException("The directory cannot be resolved", e);
        } catch (Exception e) {
            throw new ItemStreamException("Cannot find content in your Excel file", e);
        }
    }

    public void setDirectory(Resource directory) {
        this.directory = directory;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setResourceItemSearch(ResourceItemSearch<BatchExcelRow> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }

    public void setForceNewFile(boolean forceNewFile) {
        this.forceNewFile = forceNewFile;
    }
}
