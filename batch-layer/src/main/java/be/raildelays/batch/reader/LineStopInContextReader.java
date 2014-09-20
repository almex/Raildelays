package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.logging.Logger;
import be.raildelays.logging.LoggerFactory;
import be.raildelays.repository.LineStopDao;
import be.raildelays.service.RaildelaysService;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.IteratorItemReader;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Retrieve LineStop corresponding to the train id/date parameters.
 * This implementation can only be ran once.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.SkipDelayGreaterThanProcessor
 * @since 1.2
 */
public class LineStopInContextReader implements ItemStreamReader<LineStop> {

    @Resource
    private LineStopDao lineStopDao;

    private List<Long> lineStopIds = new ArrayList<>();

    private IteratorItemReader<Long> iterator;

    private static final String KEY_NAME = "listOfIds";

    private static final Logger LOGGER = LoggerFactory.getLogger("Lsr", LineStopInContextReader.class);


    @Override
    public LineStop read() throws Exception {
        LineStop result = null;
        Long id = iterator.read();

        if (id != null) {
            result = lineStopDao.findOne(id);
        }

        return result;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(KEY_NAME)) {
            lineStopIds = (List<Long>) executionContext.get(KEY_NAME);
        } else if (lineStopIds == null) {
            lineStopIds = Collections.emptyList();
        }

        iterator = new IteratorItemReader<>(lineStopIds);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (!lineStopIds.isEmpty()) {
            executionContext.put(KEY_NAME, lineStopIds);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (lineStopIds != null) {
                lineStopIds.clear();
            }
        } catch (UnsupportedOperationException e) {
            LOGGER.warn("lineStopIds cannot be cleared because the implementation doesn't support the operation. " +
                    "We generate then a new list.");
            lineStopIds = Collections.emptyList();
        }
    }

    public void setLineStopDao(LineStopDao lineStopDao) {
        this.lineStopDao = lineStopDao;
    }

    public void setLineStopIds(List<Long> lineStopIds) {
        this.lineStopIds = lineStopIds;
    }
}
