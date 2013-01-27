package be.raildelays.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.dozer.Mapper;
import org.springframework.batch.item.ItemProcessor;

import be.raildelays.domain.Sens;
import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;

public class RailtimeItemProcessor implements ItemProcessor<String, RouteLogDTO> {

	@Resource
	Mapper mapper;
	
	@Resource
	Validator validator;
	
	private RailtimeItemReader arrivalReader;

	private RailtimeItemReader departureReader;
	
	private Date date;

	private Sens sens;

	@Override
	public RouteLogDTO process(final String item) throws Exception {
		List<ServedStopDTO> cache = new ArrayList<>();	
		
		Direction arrivalDirection = arrivalReader.read();		
		Direction departureDirection = departureReader.read();		
		
		subProcess(date, arrivalDirection, cache);
		
		return subProcess(date, departureDirection, cache);
	}

	private RouteLogDTO subProcess(final Date date, final Direction item, final List<ServedStopDTO> cache) {
		RouteLogDTO result = new RouteLogDTO(item.getTrain().getIdRailtime(), date);
		LineStop previous = null;
		
		for (Step step : item.getSteps()) {
			Train train = new Train(item.getTrain().getIdRailtime());
			Station station = new Station(step.getStation().getName());
			LineStop lineStop = new LineStop(date, train, station); 
			
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
		}
		
		return result;
	}

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
