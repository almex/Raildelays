package be.raildelays.batch.reader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import be.raildelays.domain.railtime.Direction;

/**
 * Composition of {@link FlatFileItemReader} and two {@link RailtimeItemReader}.
 * 
 * This reader is restartable from the last FAILED {@link Chunk}.
 * 
 * @author Almex
 */
public class CompositeRailtimeItemReader extends CompositeItemStream implements ItemReader<List<? extends Direction>>, InitializingBean {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeRailtimeItemReader.class);
	
	private RailtimeItemReader departureReader;
	
	private RailtimeItemReader arrivalReader;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(arrivalReader, "You must provide a arrivalReader");
		Assert.notNull(departureReader, "You must provide a departureReader");
		
		LOGGER.debug("Reader initialized with arrivalReader={} and departureReader={}", arrivalReader, departureReader);
	}

	public List<? extends Direction> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {	
		List<Direction> result = null;	 

		Direction departureDirection = departureReader.read();
		Direction arrivalDirection = arrivalReader.read();
		
		if (departureDirection != null && arrivalDirection != null) {			
			result = new ArrayList<>();
			result.add(departureDirection); 
			result.add(arrivalDirection);
		}
		
		return result;
	}

	public void setArrivalReader(RailtimeItemReader arrivalReader) {
		this.arrivalReader = arrivalReader;
	}

	public void setDepartureReader(RailtimeItemReader departureReader) {
		this.departureReader = departureReader;
	}

}
