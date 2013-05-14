package be.raildelays.batch.reader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStreamReader;
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
			.getLogger(ItemReader.class);
	
	private RailtimeItemReader arrivalReader;

	private RailtimeItemReader departureReader;

	private FlatFileItemReader<String> fileReader;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(arrivalReader, "You must provide a arrivalReader");
		Assert.notNull(departureReader, "You must provide a departureReader");
		Assert.notNull(fileReader, "You must provide a fileReader");
		register(fileReader);
	}

	public List<? extends Direction> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {	
		List<Direction> result = null;
		String trainId = fileReader.read();		
		
		LOGGER.info("trainId={}", trainId);
		
		if (trainId != null) {
			// From this point we consider that we can continue to read next item.
			// No matter if we can retrieve some Direction or not from Railtime.
			// So we return a non null value to satisfy ItemReader contract.
			result = new ArrayList<>(); 
			
			departureReader.setTrainId(trainId);
			arrivalReader.setTrainId(trainId);
			
			Direction arrivalDirection = arrivalReader.read();
			Direction departureDirection = departureReader.read();
			
			if (departureDirection != null && arrivalDirection != null) {
				result.add(departureDirection); 
				result.add(arrivalDirection);
			}
		}
		
		return result;
	}

	public void setArrivalReader(RailtimeItemReader arrivalReader) {
		this.arrivalReader = arrivalReader;
	}

	public void setDepartureReader(RailtimeItemReader departureReader) {
		this.departureReader = departureReader;
	}

	public void setFileReader(FlatFileItemReader<String> fileReader) {
		this.fileReader = fileReader;
	}

}
