package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.item.ExecutionContext;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorListener {

    public static final String FILENAME_SUFFIX_KEY = "resource.filename.suffix";

    private ExecutionContext context;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.context = stepExecution.getExecutionContext();
    }

    @BeforeWrite
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        if (!items.isEmpty()) {
            // Retrieve first element of what would be written
            BatchExcelRow item = items.get(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String suffix = formatter.format(item.getDate());

            context.putString(FILENAME_SUFFIX_KEY, suffix);
        }
    }
}
