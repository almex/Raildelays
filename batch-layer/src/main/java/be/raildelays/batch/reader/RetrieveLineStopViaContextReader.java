package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.service.RaildelaysService;
import org.springframework.batch.item.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Retrieve LineStop corresponding to the train id stored in the {@link ExecutionContext}
 * and consume it.
 *
 * @author Almex
 * @since 1.2
 * @see be.raildelays.batch.processor.FilterAndStoreProcessor
 */
public class RetrieveLineStopViaContextReader implements ItemStreamReader<LineStop> {

    private Long trainId;

    private String keyName;

    private Date date;

    @Resource
    private RaildelaysService service;

    @Override
    public LineStop read() throws Exception {
        return service.searchLineStopByTrain(trainId, date);
    }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(keyName)) {
            trainId = executionContext.getLong(keyName);
            /*
             * When we have read the LineStop we consumed it so we must remove it from context.
             * Then on next iteration the next read will return null and terminate the process.
             */
            executionContext.remove(keyName);
        } else {
            trainId = null;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

     @Override
    public void close() throws ItemStreamException {

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
