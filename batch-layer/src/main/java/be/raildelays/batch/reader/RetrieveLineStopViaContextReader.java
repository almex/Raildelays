package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.service.RaildelaysService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Retrieve LineStop corresponding to the train id stored in the {@link ExecutionContext}
 * and consume it.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.FilterAndStoreProcessor
 * @since 1.2
 */
public class RetrieveLineStopViaContextReader implements ItemReader<LineStop> {

    private String keyName;

    private Date date;

    @Resource
    private RaildelaysService service;

    private ExecutionContext executionContext;

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        executionContext = jobExecution.getExecutionContext();
    }


    @Override
    public LineStop read() throws Exception {
        Long trainId = null;

        if (executionContext.containsKey(keyName)) {
            trainId = executionContext.getLong(keyName);
            /*
             * When we have read the LineStop we consumed it so we must remove it from context.
             * Then on next iteration the next read will return null and terminate the process.
             */
            executionContext.remove(keyName);
        }

        return service.searchLineStopByTrain(trainId, date);
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public void setService(RaildelaysService service) {
        this.service = service;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
