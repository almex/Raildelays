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

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.service.RaildelaysService;

public class DelaysItemReader implements ItemReader<List<LineStop>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DelaysItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private String stationA;
	
	private String stationB;
	
	private Date date;

	public List<LineStop> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		List<LineStop> result = null;
		
		LOGGER.debug("Searching delays for date={}", date);
		
		if (date != null) {
			result = service.searchDelaysBetween(date, new Station(stationA), new Station(stationB), 15);
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
