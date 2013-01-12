package be.raildelays.service.impl;

import java.io.Reader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.RailtimeTrainDao;
import be.raildelays.repository.StationDao;
import be.raildelays.service.RaildelaysGrabberService;

/**
 * 
 * We match Train by Railtime id.<br/>
 * We match Station by English name.<br/>
 * We retrieve {@link LineStop} to check if they already exist before doing a
 * request.
 * 
 * @author Almex
 */
@Service(value = "grabberService")
@Transactional
public class RailtimeGrabberService implements RaildelaysGrabberService {

	@Resource
	private RequestStreamer streamer;

	@Resource
	private LineStopDao lineStopDao;

	@Resource
	private RailtimeTrainDao railtimeTrainDao;

	@Resource
	private StationDao stationDao;

	@Resource
	private Mapper mapper;
	
	@Resource
	private Validator validator;
	

	private Logger log = Logger.getLogger(RailtimeGrabberService.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LineStop> grabTrainLine(String idTrain, Date date) {
		Map<Station, LineStop> result = new HashMap<>();
		List<LineStop> lineStops = lineStopDao.findByTrain(idTrain, date);
		
		if (lineStops.isEmpty()) {

			//-- 1) We do request/mapping for departures
			result.putAll(requestAndParse(idTrain, date, Sens.DEPARTURE));
			//-- 2) We do request/mapping with arrivals
			result.putAll(requestAndParse(idTrain, date, Sens.ARRIVAL));
			
			//-- 3) Persist line stops
			for(LineStop lineStop : result.values()) {
				lineStopDao.save(lineStop);
			}
			
			return result.values();
		} else {
			log.debug("The result already exists for idTrain="+idTrain+" and date="+date);
			
			return lineStops;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<LineStop> grabTrainLine(String idTrain) {
		return grabTrainLine(idTrain, new Date());
	}

	private Map<Station, LineStop> requestAndParse(String idTrain, Date date, Sens sens) {
		return requestAndParse(new HashMap<Station, LineStop>(),idTrain, date, sens);
	}
	
	private Map<Station, LineStop> requestAndParse(Map<Station, LineStop> cache, String idTrain, Date date, Sens sens) {
		// -- Create a request to target Railtime
		Reader englishStream = streamer.getDelays(idTrain, date,
				Language.ENGLISH.getRailtimeParameter(),
				Sens.ARRIVAL.getRailtimeParameter());

		// -- Parse the content
		StreamParser parser = new RailtimeStreamParser(englishStream);
		Direction direction = parser.parseDelay(idTrain, date);

		return mapStepToLineStop(cache, direction, sens);
	}

	private Map<Station, LineStop> mapStepToLineStop(Map<Station, LineStop> cache, Direction direction, Sens sens) {

		for (Step step : direction.getSteps()) {
			// -- Map the result to an entity
			LineStop lineStop = mapper.map(step, LineStop.class);

			// -- Map manually translation

			// -- Retrieve/Create/Update a station from the lineStopDao
			Station station = createOrRetrieveStation(step.getStation().getName());
			
			//-- Check in cache if its already exists or not
			if(cache.containsKey(station)) {
				lineStop = cache.get(station);
			} else {
				lineStop.setStation(station);
			}

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

			// -- Retrieve/Create/Update a train from the lineStopDao
			RailtimeTrain train = new RailtimeTrain(direction.getTrain().getIdRailtime());
			train.setEnglishName(direction.getTrain().getIdRailtime());
			
			lineStop.setTrain(createOrRetrieveRailtimeTrain(train));

			// -- Persist and return the result
			cache.put(station, lineStop);
		}

		return cache;
	}
	
	private RailtimeTrain createOrRetrieveRailtimeTrain(RailtimeTrain train) {
		RailtimeTrain persistedTrain = railtimeTrainDao.findOne(train.getRailtimeId());
		
		if (persistedTrain == null) {
			persistedTrain = railtimeTrainDao.save(train);
		}
		
		return persistedTrain;
	}
	
	private Station createOrRetrieveStation(String stationName) {
		Station persistedStation = stationDao.findByEnglishName(stationName);
		
		if (persistedStation == null) {
			Station station = new Station(stationName);
			
			persistedStation = stationDao.save(station);
		}
		
		return persistedStation;
	}

}
