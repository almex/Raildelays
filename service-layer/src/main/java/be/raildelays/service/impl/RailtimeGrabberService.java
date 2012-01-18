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
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import be.raildelays.service.RaildelaysGrabberService;

@Service(value="grabberService")
//@Transactional
public class RailtimeGrabberService implements RaildelaysGrabberService {

	@Autowired
	private RequestStreamer streamer;

	@Autowired
	private LineStopDao lineStopDao;
	
	@Autowired
	private TrainDao trainDao;
	
	@Autowired
	private StationDao stationDao;
	
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
				
		//-- 1) We do request/mapping with arrivals
		//-- 2) We do request/mapping for departures
		
		for(Step step : direction.getSteps()) {
			//-- Map the result to an entity
			LineStop lineStop =  mapper.map(step, LineStop.class);
			
			//-- Map manually translation
			lineStop.setStation(stationDao.createOrRetrieveStation(step.getStation().getName()));
			
			//-- Retrieve/Create/Update a station from the lineStopDao
			//-- Retrieve/Create/Update a train from the lineStopDao
			
			lineStop.setTrain(trainDao.createOrRetrieveRailtimeTrain(new RailtimeTrain(direction.getTrain().getIdRailtime())));
			
			//-- Persist and return the result
			result.add(lineStopDao.createLineStop(lineStop));
		}	
		
		return result;
	}

}
