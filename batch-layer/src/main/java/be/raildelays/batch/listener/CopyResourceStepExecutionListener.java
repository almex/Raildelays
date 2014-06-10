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
public class CopyResourceStepExecutionListener implements ItemWriteListener<BatchExcelRow>, ItemReadListener<LineStop>, StepExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyResourceStepExecutionListener.class);

    protected ResourceAwareItemStream source;

    protected ResourceAwareItemStream destination;

    @Override
    public void beforeStep(StepExecution stepExecution) {

    }

    @Override
    public void beforeRead() {
        copyResource();
    }

    @Override
    public void afterRead(LineStop item) {
        copyResource();
    }

    @Override
    public void onReadError(Exception ex) {
        copyResource();
    }

    @Override
    public void beforeWrite(List<? extends BatchExcelRow> items) {
        copyResource();
    }

    @Override
    public void afterWrite(List<? extends BatchExcelRow> items) {
        copyResource();
    }

    @Override
    public void onWriteError(Exception exception, List<? extends BatchExcelRow> items) {
        copyResource();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    private void copyResource() {
        if (source.getResource() != null) {
            destination.setResource(source.getResource());
        }
    }

    public void setSource(ResourceAwareItemStream source) {
        this.source = source;
    }

    public void setDestination(ResourceAwareItemStream destination) {
        this.destination = destination;
    }
}
