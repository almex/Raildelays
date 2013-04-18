package be.raildelays.batch.reader;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import be.raildelays.service.RaildelaysService;

/**
 * Search all dates stored in the database starting from the last date.
 * 
 * @author Almex
 */
public class DatabaseDatesItemReader implements ItemStreamReader<Date>, InitializingBean  {

	private static final String LAST_DATE = "last.date";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private Iterator<Date> iterator;
	
	private Date lastDate;

	@Override
	public void afterPropertiesSet() throws Exception {
		// Validate all job parameters
		Assert.notNull(lastDate, "You must provide the lastDate parameter to this Reader.");
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {		
		
		if (iterator == null) {			
			lastDate = (Date) executionContext.get(LAST_DATE);
			
			LOGGER.debug("Opening the stream with date={}", lastDate);
			
			List<Date> dates = service.searchAllDates(lastDate);
			
			LOGGER.trace("Retrieved {} dates", dates.size());
			
			iterator = dates.iterator();
			
			executionContext.put(LAST_DATE, null);
		}
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		LOGGER.debug("Updating the date={} within the execution context", lastDate);
		
		executionContext.put(LAST_DATE, lastDate);				
	}

	@Override
	public void close() throws ItemStreamException {
		LOGGER.debug("Closing the stream...");		
		iterator = null;
	}

	public Date read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Date result = lastDate = null;
		
		if (iterator.hasNext()) {
			result = lastDate = iterator.next();
			
			LOGGER.debug("Iterator have one more date={}", result);
		}
		
		return result;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

}
