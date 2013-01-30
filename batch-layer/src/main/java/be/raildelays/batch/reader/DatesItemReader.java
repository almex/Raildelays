package be.raildelays.batch.reader;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import be.raildelays.service.RaildelaysService;

public class DatesItemReader implements ItemStreamReader<Date> {

	private static final String LAST_DATE = "last.date";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DatesItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private StepExecution stepExecution;
	
	private Iterator<Date> iterator;
	
	private Date date;

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {		
		
		if (iterator == null) {			
			date = (Date) executionContext.get(LAST_DATE);
			
			LOGGER.debug("Opening the stream with date={}", date);
			
			List<Date> dates = service.searchAllDates(date);
			
			LOGGER.trace("Retrieved {} dates", dates.size());
			
			iterator = dates.iterator();
			
			executionContext.put(LAST_DATE, null);
		}
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		LOGGER.debug("Updating the date={} within the execution context", date);
		
		executionContext.put(LAST_DATE, date);				
	}

	@Override
	public void close() throws ItemStreamException {
		LOGGER.debug("Closing the stream...");		
		iterator = null;
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public Date read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Date result = date = null;
		
		if (iterator.hasNext()) {
			result = date = iterator.next();
			
			LOGGER.debug("Iterator have one more date={}", result);
		}
		
		return result;
	}

}
