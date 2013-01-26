package be.raildelays.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.dozer.Mapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;

import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;

public class RailtimeItemProcessor implements ItemProcessor<String, List<LineStop>> {

	@Resource
	Mapper mapper;
	
	@Resource
	Validator validator;
	
	private RailtimeItemReader arrivalReader;

	private RailtimeItemReader departureReader;
	
	private Date date;

	private Sens sens;
	
	private StepExecution stepExecution;
	
	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public List<LineStop> process(final String item) throws Exception {
		List<LineStop> cache = new ArrayList<>();	
		
		stepExecution.getExecutionContext().put("idTrain", item);
		
		Direction arrivalDirection = arrivalReader.read();		
		Direction departureDirection = departureReader.read();		
		
		subProcess(arrivalDirection, cache);
		
		return subProcess(departureDirection, cache);
	}

	private List<LineStop> subProcess(final Direction item, final List<LineStop> cache) {
		List<LineStop> result = new ArrayList<>();
		LineStop previous = null;
		
		for (Step step : item.getSteps()) {
			Train train = new Train(item.getTrain().getIdRailtime());
			Station station = new Station(step.getStation().getName());
			LineStop lineStop = new LineStop(date, train, station); 
					
			//-- Use a cache to handle two readers (one by sens)
			int index = cache.indexOf(lineStop);
			if (index >= 0 ) {
				lineStop = cache.remove(index);
			} else {
				cache.add(lineStop);
			}
			
			// -- We are making the link to keep trace of direction
			previous = link(previous, lineStop);

			// -- Map the result to an entity
			mapper.map(step, lineStop);

			// -- TODO Map manually translation
	
			// -- Set the time stamp delay to the right sens
			TimestampDelay timestampDelay = new TimestampDelay(
					step.getTimestamp(), step.getDelay() == null ? 0L
							: step.getDelay());
			switch (sens) {
			case ARRIVAL:
				lineStop.setArrivalTime(timestampDelay);
				break;
			case DEPARTURE:
				lineStop.setDepartureTime(timestampDelay);
				break;
			}
			
			result.add(lineStop);
		}
		
		return result;
	}
	
//	@Transactional
//	public List<LineStop> grabTrainLine(String idTrain, Date date) {
//		List<LineStop> result = new ArrayList<LineStop>();
//		RailtimeTrain train = saveOrRetrieveRailtimeTrain(new RailtimeTrain(
//				idTrain));
//		List<LineStop> lineStops = lineStopDao.findByTrainAndDate(train, date);
//
//		log.debug("Grabbing for tain=" + train + " date=" + date);
//
//		if (lineStops.isEmpty()) {
//
//			// -- 1) We do request/mapping for departures
//			requestAndPersist(train, date, Sens.DEPARTURE);
//			// -- 2) We do request/mapping with arrivals
//			lineStops = requestAndPersist(train, date, Sens.ARRIVAL);
//
//		} else {
//			log.debug("The result already exists for idTrain=" + idTrain
//					+ " and date=" + date);
//
//			result = lineStops;
//		}
//
//		return result;
//	}

//	private List<LineStop> convertStepToLineStop(RailtimeTrain train,
//			Date date, Direction direction, Sens sens) {
//		List<LineStop> result = new ArrayList<>();
//
//		log.debug("direction=" + direction + " date=" + date + " sens="
//				+ sens.name());
//		
//		for (Step step : direction.getSteps()) {
//			LineStop lineStop = null;
//			
//			// -- Retrieve/Create/Update a station from the lineStopDao
//			Station station = saveOrRetrieveStation(step.getStation().getName());
//			LineStop persistedLineStop = lineStopDao
//					.findByTrainAndDateAndStation(train, date, station);
//
//			if (persistedLineStop != null) {
//				lineStop = persistedLineStop;
//			} else {
//				lineStop = new LineStop(date, train, station);
//			}
//			
//			// -- We are making the link to keep trace of direction
//			previous = link(previous, lineStop);
//
//			// -- Map the result to an entity
//			mapper.map(step, lineStop);
//
//			// -- TODO Map manually translation
//
//			// -- Set the time stamp delay to the right sens
//			TimestampDelay timestampDelay = new TimestampDelay(
//					step.getTimestamp(), step.getDelay() == null ? 0L
//							: step.getDelay());
//
//			switch (sens) {
//			case ARRIVAL:
//				lineStop.setArrivalTime(timestampDelay);
//				break;
//			case DEPARTURE:
//				lineStop.setDepartureTime(timestampDelay);
//				break;
//			}
//
//			log.debug("lineStop=" + lineStop.toStringAll());
//			
//			// -- Persist and return the result
//			result.add(lineStop);			
//		}
//
//		return result;
//	}
//	
//	private List<LineStop> convertStepToLineStop(RailtimeTrain train, Date date, Direction direction) {
//		List<LineStop> result = new ArrayList<LineStop>();
//		LineStop previous = null;
//		
//		for (Step step : direction.getSteps()) {
//			LineStop lineStop = null;
//			
//			// -- Retrieve/Create/Update a station from the lineStopDao
//			Station station = saveOrRetrieveStation(step.getStation().getName());
//			LineStop persistedLineStop = lineStopDao
//					.findByTrainAndDateAndStation(train, date, station);
//
//			if (persistedLineStop != null) {
//				lineStop = persistedLineStop;
//			} else {
//				lineStop = new LineStop(date, train, station);
//			}
//
//			log.debug("lineStop=" + lineStop.toStringAll());
//			
//			// -- Persist and return the result
//			result.add(lineStop);			
//		}
//
//		return result;
//	}

	private static LineStop link(LineStop previous, LineStop current) {
		
		if (previous != null) {
			previous.setNext(current);
		}
		
		if (current != null) {
			current.setPrevious(previous);
		}
		
		return current;
	}
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setSens(Sens sens) {
		this.sens = sens;
	}
	
	public void setArrivalReader(RailtimeItemReader arrivalReader) {
		this.arrivalReader = arrivalReader;
	}

	public void setDepartureReader(RailtimeItemReader departureReader) {
		this.departureReader = departureReader;
	}

}
