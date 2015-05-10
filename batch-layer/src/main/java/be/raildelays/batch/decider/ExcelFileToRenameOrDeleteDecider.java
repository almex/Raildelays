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
