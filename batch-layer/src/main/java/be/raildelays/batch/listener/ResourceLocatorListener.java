package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Almex
 */
public class ResourceLocatorListener implements StepExecutionListener, ItemWriteListener<BatchExcelRow> {

    public static final String FILENAME_SUFFIX_KEY = "resource.filename.suffix";

    private ExecutionContext context;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.context = stepExecution.getExecutionContext();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Override
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        if (!items.isEmpty()) {
            // Retrieve first element of what would be written
            BatchExcelRow item = items.get(0);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            String suffix = formatter.format(item.getDate());

            context.putString(FILENAME_SUFFIX_KEY, suffix);
        }
    }

    @Override
    public void afterWrite(List<? extends BatchExcelRow> items) {
        context.putString(FILENAME_SUFFIX_KEY, null);
    }

    @Override
    public void onWriteError(Exception exception, List<? extends BatchExcelRow> items) {

    }
}
