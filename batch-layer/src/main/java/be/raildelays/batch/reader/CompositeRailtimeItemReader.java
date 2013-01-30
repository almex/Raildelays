package be.raildelays.batch.reader;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.Resource;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;

public class CompositeRailtimeItemReader implements ItemStreamReader<RouteLogDTO> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CompositeRailtimeItemReader.class);
	
	@javax.annotation.Resource
	Validator validator;
	
	private RailtimeItemReader arrivalReader;

	private RailtimeItemReader departureReader;

	private FlatFileItemReader<String> fileReader;
	
	private String date;

	private Resource resource;
	
	private StepExecution stepExecution;
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void open(ExecutionContext executionContext)
			throws ItemStreamException {	
		fileReader.setResource(resource);
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

	public RouteLogDTO read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {	
		RouteLogDTO result = null;
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		Date date = formater.parse(this.date);	
		String trainId = fileReader.read();		
		
		if (trainId != null) {
			LOGGER.debug("Processing date={} trainId={}...", this.date, trainId);
			
			departureReader.setTrainId(trainId);
			departureReader.setDate(date);
			arrivalReader.setTrainId(trainId);
			arrivalReader.setDate(date);
			
			Direction arrivalDirection = arrivalReader.read();
			Direction departureDirection = departureReader.read();
			
			if (departureDirection != null && arrivalDirection != null) {
				result = subProcess(date, departureDirection, arrivalDirection);
			}
		}
		
		return result;
	}

	private RouteLogDTO subProcess(final Date date, Direction departureDirection, final Direction arrivalDirection) {
		RouteLogDTO result = new RouteLogDTO(arrivalDirection.getTrain().getIdRailtime(), date);
		
		for (Step arrivalStep : arrivalDirection.getSteps()) {
			int index = arrivalDirection.getSteps().indexOf(arrivalStep);
			Step departureStep = departureDirection.getSteps().get(index);
						
			ServedStopDTO stop = new ServedStopDTO(arrivalStep.getStation().getName(),
					departureStep.getTimestamp(), departureStep.getDelay(),
					arrivalStep.getTimestamp(), arrivalStep.getDelay(), arrivalStep.isCanceled() || departureStep.isCanceled());
			
			// We validate the result
			validator.validate(stop);
			
			result.addStop(stop);
		}
		
		return result;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

}
