package be.raildelays.service.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Train;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;
import be.raildelays.repository.LineStopDao;
import be.raildelays.service.RaildelaysGrabberService;

@Service(value="grabberService")
public class RailtimeGrabberService implements RaildelaysGrabberService {

	@Autowired
	private RequestStreamer streamer;

	@Autowired
	private LineStopDao repository;
	
	@Autowired
	Mapper mapper;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LineStop> grabTrainLine(String idTrain, Date date) {
		List<LineStop> result = new ArrayList<>();
		
		//-- Create a request to target Railtime
		Reader englishStream = streamer.getDelays(idTrain, date, Language.ENGLISH.getRailtimeParameter());
		
		//-- Parse the content
		StreamParser parser = new RailtimeStreamParser(englishStream);
		Direction direction = parser.parseDelay(idTrain, date);
				
		for(Step step : direction.getSteps()) {
			//-- Map the result to an entity
			LineStop lineStop =  mapper.map(step, LineStop.class);
			
			//-- Map manually translation
			lineStop.getStation().setEnglishName(step.getStation().getName());
			
			//-- Retrieve/Create/Update a station from the repository
			//-- Retrieve/Create/Update a train from the repository
			lineStop.setTrain(new Train(direction.getTrain().getIdRailtime()));
			
			//-- Persist and return the result
			result.add(repository.createLineStop(lineStop));
			break;
		}	
		
		return result;
	}

}
