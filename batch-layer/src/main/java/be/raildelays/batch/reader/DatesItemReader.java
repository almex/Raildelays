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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import be.raildelays.service.RaildelaysService;

public class DatesItemReader implements ItemReader<Date>, ItemStreamReader<Date> {

	private static final String DATES_ITERATOR = "datesIterator";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DatesItemReader.class);

	@Resource
	private RaildelaysService service;
	
	private StepExecution stepExecution;
	
	private Iterator<Date> iterator;

	@SuppressWarnings("unchecked")
	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		
		iterator = (Iterator<Date>) executionContext.get(DATES_ITERATOR); 
		
		if (iterator == null) {
			LOGGER.debug("Iterator does not exists searching in the database...");
			
			iterator = service.searchAllDates().iterator();
			
			executionContext.put(DATES_ITERATOR, iterator);
		}
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		LOGGER.debug("Updating the iterator within the execution context...");
		
		executionContext.put(DATES_ITERATOR, iterator);				
	}

	@Override
	public void close() throws ItemStreamException {
		LOGGER.debug("Closing the iterator...");		
	}
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public Date read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		Date result = null;
		
		if (iterator.hasNext()) {
			result = iterator.next();
			
			LOGGER.debug("Iterator have one more date={}", result);
		}
		
		return result;
	}

}
