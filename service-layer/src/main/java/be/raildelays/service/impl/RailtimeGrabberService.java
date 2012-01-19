package be.raildelays.service.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.TimestampDelay;
import be.raildelays.domain.railtime.Direction;
import be.raildelays.domain.railtime.Step;
import be.raildelays.httpclient.RequestStreamer;
import be.raildelays.parser.StreamParser;
import be.raildelays.parser.impl.RailtimeStreamParser;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
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
// @Transactional
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
		
		lineStopDao.retrieveLineStop(idTrain, date);

		// -- 1) We do request/mapping with arrivals
		result.addAll(requestAndParse(idTrain, date, Sens.ARRIVAL));
		// -- 2) We do request/mapping for departures
		result.addAll(requestAndParse(idTrain, date, Sens.DEPARTURE));

		return result;
	}

	private List<LineStop> requestAndParse(String idTrain, Date date, Sens sens) {
		// -- Create a request to target Railtime
		Reader englishStream = streamer.getDelays(idTrain, date,
				Language.ENGLISH.getRailtimeParameter(),
				Sens.ARRIVAL.getRailtimeParameter());

		// -- Parse the content
		StreamParser parser = new RailtimeStreamParser(englishStream);
		Direction direction = parser.parseDelay(idTrain, date);

		return mapStepToLineStop(direction, sens);
	}

	private List<LineStop> mapStepToLineStop(Direction direction, Sens sens) {
		List<LineStop> result = new ArrayList<>();

		for (Step step : direction.getSteps()) {
			// -- Map the result to an entity
			LineStop lineStop = mapper.map(step, LineStop.class);

			// -- Map manually translation

			// -- Retrieve/Create/Update a station from the lineStopDao
			lineStop.setStation(stationDao.createOrRetrieveStation(step
					.getStation().getName()));

			TimestampDelay timestampDelay = new TimestampDelay(
					step.getTimestamp(), step.getDelay());

			switch (sens) {
			case ARRIVAL:
				lineStop.setArrivalTime(timestampDelay);
				break;
			case DEPARTURE:
				lineStop.setDepartureTime(timestampDelay);
				break;
			}

			// -- Retrieve/Create/Update a train from the lineStopDao
			lineStop.setTrain(trainDao
					.createOrRetrieveRailtimeTrain(new RailtimeTrain(direction
							.getTrain().getIdRailtime())));

			// -- Persist and return the result
			result.add(lineStopDao.createLineStop(lineStop));
		}

		return result;
	}

}
