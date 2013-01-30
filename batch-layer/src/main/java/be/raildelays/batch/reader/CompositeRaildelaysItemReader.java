package be.raildelays.batch.reader;

import java.util.Date;
import java.util.List;

import javax.validation.Validator;

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

import be.raildelays.domain.entities.LineStop;

public class CompositeRaildelaysItemReader implements ItemStreamReader<List<LineStop>> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeRaildelaysItemReader.class);

	@javax.annotation.Resource
	Validator validator;

	private DelaysItemReader delaysItemReader;
	
	private DatesItemReader datesItemReader;
	
	private String stationA;
	
	private String stationB;

	private StepExecution stepExecution;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {
		delaysItemReader.setStationA(stationA);
		delaysItemReader.setStationB(stationB);
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
		List<LineStop> result = null;		

		Date date = datesItemReader.read();
		if (date != null) {
			delaysItemReader.setDate(date);
			result = delaysItemReader.read();
			
			LOGGER.debug("Found {} delays for {}", result.size(), date);
		}

		return result;
	}

	public void setStationA(String stationA) {
		this.stationA = stationA;
	}

	public void setStationB(String stationB) {
		this.stationB = stationB;
	}

	public void setDelaysItemReader(DelaysItemReader delaysItemReader) {
		this.delaysItemReader = delaysItemReader;
	}

	public void setDatesItemReader(DatesItemReader datesItemReader) {
		this.datesItemReader = datesItemReader;
	}

}
