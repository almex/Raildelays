package be.raildelays.batch.reader;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
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
public class DelaysItemReader implements ItemReader<List<LineStop>>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DelaysItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private String stationA;
	
	private String stationB;
	
	private Date date;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Validate all job parameters
		Assert.notNull(stationA, "You must provide the stationA parameter to this Reader.");
		Assert.notNull(stationB, "You must provide the stationB parameter to this Reader.");
	}

	public List<LineStop> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		List<LineStop> result = null;
		
		LOGGER.debug("Searching delays for date={}", date);
		
		result = service.searchDelaysBetween(date, new Station(stationA), new Station(stationB), 15);
		
		if (result.isEmpty()) {
			result = null; // To apply the ItemReader contract
		}

		return result;
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

}
