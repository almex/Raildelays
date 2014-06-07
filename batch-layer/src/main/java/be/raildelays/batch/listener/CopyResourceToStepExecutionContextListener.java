package be.raildelays.batch.listener;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.batch.support.ResourceAwareItemStream;
import be.raildelays.domain.entities.LineStop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.item.ExecutionContext;

import java.util.List;

/**
 * @author Almex
 */
public class CopyResourceToStepExecutionContextListener implements ItemWriteListener<BatchExcelRow>, ItemReadListener<LineStop>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyResourceToStepExecutionContextListener.class);

    protected ResourceAwareItemStream itemStream;

    protected String contextKey;

    private ExecutionContext executionContext;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.executionContext = stepExecution.getExecutionContext();
    }

    @Override
    public void beforeRead() {
        copyResourceToStepExecutionContext();
    }

    @Override
    public void afterRead(LineStop item) {
        copyResourceToStepExecutionContext();
    }

    @Override
    public void onReadError(Exception ex) {
        copyResourceToStepExecutionContext();
    }

    @Override
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        copyResourceToStepExecutionContext();
    }

    @Override
    public void afterWrite(List<? extends BatchExcelRow> items) {
        copyResourceToStepExecutionContext();
    }

    @Override
    public void onWriteError(Exception exception, List<? extends BatchExcelRow> items) {
        copyResourceToStepExecutionContext();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    private void copyResourceToStepExecutionContext() {
        if (itemStream.getResource() != null) {
            executionContext.put(contextKey, itemStream.getResource());
            LOGGER.debug("Copied resource={} to step execution context with key={}.", itemStream.getResource().getDescription(), contextKey);
        }
    }

    public void setContextKey(String contextKey) {
        this.contextKey = contextKey;
    }

    public void setItemStream(ResourceAwareItemStream itemStream) {
        this.itemStream = itemStream;
    }
}
