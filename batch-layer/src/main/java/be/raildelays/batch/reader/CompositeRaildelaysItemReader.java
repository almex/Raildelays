package be.raildelays.batch.reader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import be.raildelays.domain.entities.LineStop;

/**
 * Composition between {@link DelaysItemReader} and {@link DatabaseDatesItemReader}.
 * 
 * @author Almex
 */
public class CompositeRaildelaysItemReader implements ItemStreamReader<List<LineStop>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ItemReader.class);

	@javax.annotation.Resource
	Validator validator;

	private DelaysItemReader delaysItemReader;
	
	private DatabaseDatesItemReader datesItemReader;

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		datesItemReader.open(executionContext);
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		datesItemReader.update(executionContext);		
	}

	@Override
	public void close() throws ItemStreamException {
		datesItemReader.close();
	}

	public List<LineStop> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		List<LineStop> result = null; // The end of this reader is when we have no more date	
		Date date = datesItemReader.read();
		
		if (date != null) {			
			delaysItemReader.setDate(date);
			
			List<LineStop> lineStops = delaysItemReader.read();
			
			// At this point we must return a non null value to continue reading
			result = new ArrayList<>();
			
			if (lineStops != null) {
				result.addAll(lineStops);
			}
			
			LOGGER.debug("Found {} delays for {}", result.size(), date);
		}

		return result;
	}

	public void setDelaysItemReader(DelaysItemReader delaysItemReader) {
		this.delaysItemReader = delaysItemReader;
	}

	public void setDatesItemReader(DatabaseDatesItemReader datesItemReader) {
		this.datesItemReader = datesItemReader;
	}

}
