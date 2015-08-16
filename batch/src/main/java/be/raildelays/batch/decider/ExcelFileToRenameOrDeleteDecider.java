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

package be.raildelays.batch.decider;

import be.raildelays.batch.ExcelFileUtils;
import be.raildelays.domain.xls.ExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;

/**
 * Read the content of an Excel file:
 * <ul>
 * <li>If the first row is empty then {@code DELETE} is returned as {@link ExitStatus}.</li>
 * <li>If the first row is not empty then {@code RENAME} is returned as {@link ExitStatus} and the new file name is stored in the
 * step {@link ExecutionContext} via the key 'excel.filename'.</li>
 * </ul>
 *
 * @author Almex
 * @since 1.2
 */
public class ExcelFileToRenameOrDeleteDecider extends AbstractReadAndDecideTasklet<ExcelRow> implements InitializingBean {

    private String fileNamePrefix;
    private Resource directory;
    private String contextKey;
    public static final ExitStatus RENAME = new ExitStatus("RENAME");
    public static final ExitStatus DELETE = new ExitStatus("DELETE");


    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(this.reader, "The 'reader' property must be provided");
        Assert.notNull(this.directory, "The 'directory' property must be provided");
        Assert.notNull(this.contextKey, "The 'contextKey' property must be provided");
        Assert.notNull(this.fileNamePrefix, "The 'fileNamePrefix' property must be provided");
    }

    @Override
    public ExitStatus doRead(StepContribution contribution, ExecutionContext context, ExcelRow item) throws Exception {
        ExitStatus exitStatus;

        if (item.getDate() != null) {
            File file = directory.getFile();
            String fileName = ExcelFileUtils.getFileName(file);
            String fileExtension = ExcelFileUtils.getFileExtension(file);
            File finalFile = ExcelFileUtils.getFile(file.getParentFile(), fileName, item.getDate(), fileExtension);

            context.put(contextKey, finalFile.getName());
            exitStatus = RENAME;
        } else {
            exitStatus = DELETE;
        }

        return exitStatus;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public void setDirectory(Resource directory) {
        this.directory = directory;
    }

    public void setContextKey(String contextKey) {

        this.contextKey = contextKey;
    }
}
