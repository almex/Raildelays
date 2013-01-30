package be.raildelays.batch.reader;

import java.util.Date;
import java.util.List;

import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import be.raildelays.domain.entities.LineStop;

public class CompositeRaildelaysItemReader implements
		ItemReader<List<LineStop>> {

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

	public List<LineStop> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		List<LineStop> result = null;
		
		delaysItemReader.setStationA(stationA);
		delaysItemReader.setStationB(stationB);

		Date date = datesItemReader.read();
		if (date != null) {
			stepExecution.getExecutionContext().put("date", date);
			result = delaysItemReader.read();
			
			LOGGER.debug("Found {} line stops for {}", result.size(), date);
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
