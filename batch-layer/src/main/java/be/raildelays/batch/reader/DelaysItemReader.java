package be.raildelays.batch.reader;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.service.RaildelaysService;

/**
 * Search delays for train going from A to B or B to A for a certain date.
 * 
 * @author Almex
 */
public class DelaysItemReader implements ItemReader<LineStop>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DelaysItemReader.class);

	@Resource
	private RaildelaysService service;

    private IteratorItemReader<LineStop> delegate;
	
	private String stationA;
	
	private String stationB;
	
	private Date date;

    private Integer threshold;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Validate all job parameters
		Assert.notNull(stationA, "You must provide the stationA parameter to this Reader.");
		Assert.notNull(stationB, "You must provide the stationB parameter to this Reader.");
	}

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        List<LineStop> result = new ArrayList<>();

        LOGGER.debug("Searching delays for date={}", date);

        if (date != null) {
            result = service.searchDelaysBetween(date, new Station(stationA), new Station(stationB), threshold);
        }

        Collections.sort(result);

        delegate = new IteratorItemReader<LineStop>(result.iterator());
    }


	public LineStop read() throws Exception {
		return delegate.read();
	}

	public void setStationA(String stationA) {
		this.stationA = stationA;
	}

	public void setStationB(String stationB) {
		this.stationB = stationB;
	}

	public void setDate(Date date) {
		this.date = date;
	}

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }
}
