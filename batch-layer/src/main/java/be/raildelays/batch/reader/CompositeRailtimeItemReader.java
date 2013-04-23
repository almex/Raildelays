package be.raildelays.batch.reader;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;

import be.raildelays.domain.railtime.Direction;

/**
 * Composition of {@link FlatFileItemReader} and two {@link RailtimeItemReader}.
 * 
 * @author Almex
 */
public class CompositeRailtimeItemReader implements ItemStreamReader<List<? extends Direction>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeRailtimeItemReader.class);
	
	@javax.annotation.Resource
	Validator validator;
	
	private RailtimeItemReader arrivalReader;

	private RailtimeItemReader departureReader;

	private FlatFileItemReader<String> fileReader;

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		fileReader.open(executionContext);
		
	}

	@Override
	public void update(ExecutionContext executionContext)
			throws ItemStreamException {
		fileReader.update(executionContext);
		
	}

	@Override
	public void close() throws ItemStreamException {
		fileReader.close();		
	}

	public List<? extends Direction> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {	
		List<Direction> result = null;
		String trainId = fileReader.read();		
		
		if (trainId != null) {
			
			departureReader.setTrainId(trainId);
			arrivalReader.setTrainId(trainId);
			
			Direction arrivalDirection = arrivalReader.read();
			Direction departureDirection = departureReader.read();
			
			if (departureDirection != null && arrivalDirection != null) {
				result = new ArrayList<>();
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
