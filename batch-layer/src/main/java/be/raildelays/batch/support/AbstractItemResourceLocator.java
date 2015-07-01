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

package be.raildelays.batch.support;

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.listener.ResourceLocatorListener;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.support.ResourceLocator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static org.springframework.batch.item.file.ExcelSheetItemWriter.Format;

/**
 * We communicate through the {@link org.springframework.batch.item.ExecutionContext} the file name used to create a new file.
 * The actual resource is used to build the final of a new file.
 * <p>
 * MaxItemCount must be a dividend of chunk-size
 * <p>
 * original: path/filemane.extension
 * result: path/filename_suffix.extension
 *
 * @author Almex
 * @see ResourceLocatorListener
 */
public abstract class AbstractItemResourceLocator implements ResourceLocator {

    public String keyName;
    protected Resource resource;
    private ResourceItemSearch<BatchExcelRow> resourceItemSearch;

    protected File getFileBasedOnSuffix(ExecutionContext context) throws IOException {
        String suffix = context.getString(ResourceLocatorListener.FILENAME_SUFFIX_KEY);
        File result = resource.getFile(); // By default we return the resource itself

        if (suffix != null) {
            String fileName = ExcelFileUtils.getFileName(resource.getFile());
            String fileExtension = ExcelFileUtils.getFileExtension(resource.getFile());

            result = ExcelFileUtils.getFile(resource.getFile().getParentFile(), fileName, suffix, fileExtension);
        }

        return result;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public File getExistingFile() throws IOException {
        File directory = resource.getFile().isDirectory() ? resource.getFile() : resource.getFile().getParentFile();
        File result = null;

        if (directory != null) {
            File[] files = directory.listFiles(pathname ->
                    pathname.getName().endsWith(Format.OLE2.getFileExtension()) ||
                            pathname.getName().endsWith(Format.OOXML.getFileExtension()));

            if (files != null) {
                for (File file : files) {
                    try {
                        //-- We search the first empty Row
                        if (resourceItemSearch.indexOf(BatchExcelRow.EMPTY, new FileSystemResource(file)) != ResourceItemSearch.EOF) {
                            result = file;
                        }
                    } catch (InvalidFormatException e) {
                        throw new IOException("Excel format not supported for this workbook!", e);
                    } catch (Exception e) {
                        throw new IOException("Cannot find content in your Excel file", e);
                    }
                }
            }
        }


        return result;
    }

    public void setResourceItemSearch(ResourceItemSearch<BatchExcelRow> resourceItemSearch) {
        this.resourceItemSearch = resourceItemSearch;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }
}
