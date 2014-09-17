package be.raildelays.batch.reader;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.service.RaildelaysService;
import org.springframework.batch.item.ItemReader;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Retrieve LineStop corresponding to the train id/date parameters.
 * This implementation can only be ran once.
 *
 * @author Almex
 * @see be.raildelays.batch.processor.FilterByDelayThresholdAndStoreTrainIdProcessor
 * @since 1.2
 */
public class ByTrainIdAndDateLineStopReader implements ItemReader<LineStop> {

    private Long trainId;

    private int count = 0;

    private Date date;

    @Resource
    private RaildelaysService service;


    @Override
    public LineStop read() throws Exception {
        LineStop result = null;

        if (count == 0) {
            result = service.searchLineStopByTrain(trainId, date);
            count++;
        }

        return result;
    }

    public void setTrainId(Long trainId) {
        this.trainId = trainId;
    }

    public void setService(RaildelaysService service) {
        this.service = service;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
