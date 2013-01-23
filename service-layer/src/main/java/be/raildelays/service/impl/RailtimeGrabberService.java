package be.raildelays.service.impl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Validator;

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
	@Transactional
	public List<LineStop> grabTrainLine(String idTrain, Date date) {
		List<LineStop> result = new ArrayList<LineStop>();
		RailtimeTrain train = saveOrRetrieveRailtimeTrain(new RailtimeTrain(
				idTrain));
		List<LineStop> lineStops = lineStopDao.findByTrainAndDate(train, date);

		log.debug("Grabbing for tain=" + train + " date=" + date);

		if (lineStops.isEmpty()) {

			// -- 1) We do request/mapping for departures
			requestAndPersist(train, date, Sens.DEPARTURE);
			// -- 2) We do request/mapping with arrivals
			lineStops = requestAndPersist(train, date, Sens.ARRIVAL);

		} else {
			log.debug("The result already exists for idTrain=" + idTrain
					+ " and date=" + date);

			result = lineStops;
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public Collection<LineStop> grabTrainLine(String idTrain) {
		return grabTrainLine(idTrain, new Date());
	}

	private List<LineStop> requestAndPersist(RailtimeTrain train, Date date,
			Sens sens) {
		List<LineStop> result = new ArrayList<LineStop>();

		for (LineStop lineStop : requestAndParse(train, date, sens)) {
			result.add(lineStopDao.save(lineStop));
		}

		return result;
	}

	private List<LineStop> requestAndParse(RailtimeTrain train, Date date,
			Sens sens) {
		// -- Create a request to target Railtime
		Reader englishStream = streamer.getDelays(train.getEnglishName(), date,
				Language.ENGLISH.getRailtimeParameter(),
				sens.getRailtimeParameter());

		// -- Parse the content
		StreamParser parser = new RailtimeStreamParser(englishStream);
		Direction direction = parser.parseDelay(train.getEnglishName(), date);

		return convertStepToLineStop(train, date, direction, sens);
	}

	private List<LineStop> convertStepToLineStop(RailtimeTrain train,
			Date date, Direction direction, Sens sens) {
		List<LineStop> result = new ArrayList<LineStop>();

		log.debug("direction=" + direction + " date=" + date + " sens="
				+ sens.name());

		for (Step step : direction.getSteps()) {
			LineStop lineStop = null;
			// -- Retrieve/Create/Update a station from the lineStopDao
			Station station = saveOrRetrieveStation(step.getStation().getName());
			LineStop persistedLineStop = lineStopDao
					.findByTrainAndDateAndStation(train, date, station);

			if (persistedLineStop != null) {
				lineStop = persistedLineStop;
			} else {
				lineStop = new LineStop();
				lineStop.setDate(date);
				lineStop.setTrain(train);
				lineStop.setStation(station);
			}

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

			log.debug("lineStop=" + lineStop);

			// -- Persist and return the result
			result.add(lineStop);
		}

		return result;
	}

	private RailtimeTrain saveOrRetrieveRailtimeTrain(RailtimeTrain train) {
		RailtimeTrain result = null;
		RailtimeTrain persistedTrain = railtimeTrainDao.findByRailtimeId(train
				.getRailtimeId());
		
		if (persistedTrain == null) {
			result = railtimeTrainDao.saveAndFlush(train);
		} else {
			result = persistedTrain;
		}

		return result;
	}

	private Station saveOrRetrieveStation(String stationName) {
		Station result = stationDao.findByEnglishName(stationName);

		if (result == null) {
			Station station = new Station(stationName);

			result = stationDao.saveAndFlush(station);
		}

		return result;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override
	@Transactional(readOnly = true)
	public Collection<LineStop> searchAllDelays(Date date, Station departure,
			Station arrival, int delayThreshold) {
		Collection<LineStop> result = new ArrayList<LineStop>();

		// result.addAll(lineStopDao.findDepartureDelays(date, departure,
		// delayThreshold));
		result.addAll(lineStopDao.findArrivalDelays(date, arrival,
				delayThreshold));

		return result;
	}

}
