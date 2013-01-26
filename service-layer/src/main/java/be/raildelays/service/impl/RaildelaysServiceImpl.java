package be.raildelays.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.validation.Validator;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import be.raildelays.domain.dto.RouteLogDTO;
import be.raildelays.domain.dto.ServedStopDTO;
import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.Station;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.LineStopDao;
import be.raildelays.repository.RailtimeTrainDao;
import be.raildelays.repository.StationDao;
import be.raildelays.repository.TrainDao;
import be.raildelays.service.RaildelaysService;

/**
 * 
 * We match Train by Railtime id.<br/>
 * We match Station by English name.<br/>
 * We retrieve {@link LineStop} to check if they already exist before doing a
 * request.
 * 
 * @author Alexis SOUMAGNE.
 */
@Service(value = "raildelaysService")
@Transactional
public class RaildelaysServiceImpl implements RaildelaysService {

	@Resource
	private LineStopDao lineStopDao;

	@Resource
	private RailtimeTrainDao railtimeTrainDao;
	
	@Resource
	private TrainDao trainDao;

	@Resource
	private StationDao stationDao;

	@Resource
	private Mapper mapper;

	@Resource
	private Validator validator;

	private Logger log = LoggerFactory.getLogger(RaildelaysServiceImpl.class);

	@Override
	@Transactional
	public List<LineStop> saveRouteLog(final RouteLogDTO routeLog) {
		List<LineStop> result = new ArrayList<>();

		log.debug("Saving timetable for train={} and date={}...",
				routeLog.getTrainId(), routeLog.getDate());

		// -- Validate our inputs
		validator.validate(routeLog);

		persist(routeLog.getDate(), routeLog.getTrainId(), routeLog.getStops());

		return result;
	}

	@Transactional
	public LineStop saveServedStop(final Date date, final String trainId,
			final ServedStopDTO stop, final LineStop previous) {

		log.debug("Saving timetable for train={}, date={} and lineStop={}...",
				trainId, date, stop);

		// -- Validate our inputs
		Assert.notNull(date, "You should provide a date for this served stop");
		Assert.hasText(trainId,
				"You should provide a train id for this served stop");
		validator.validate(stop);

		// -- Retrieve persisted version of sub-entities to avoid duplicate key
		RailtimeTrain persistedTrain = saveOrRetrieveRailtimeTrain(new RailtimeTrain(
				trainId, trainId));
		Station persistedStation = saveOrRetrieveStation(new Station(
				stop.getStationName()));
		LineStop lineStop = new LineStop(date, persistedTrain, persistedStation, previous);

		mapper.map(stop, lineStop);

		// -- Validate our output
		validator.validate(lineStop);

		return lineStopDao.save(createUpdateOrRetrieve(lineStop));
	}

	@Override
	@Transactional(readOnly = true)
	public List<LineStop> searchDelaysBetween(Date date, Station stationA,
			Station stationB, int delayThreshold) {
		List<LineStop> result = new ArrayList<LineStop>();

		result.addAll(lineStopDao.findArrivalDelays(date, stationA,
				delayThreshold));
		result.addAll(lineStopDao.findArrivalDelays(date, stationB,
				delayThreshold));

		return result;
	}

	private List<LineStop> persist(final Date date, final String trainId,
			List<? extends ServedStopDTO> stops) {
		List<LineStop> result = new ArrayList<>();
		LineStop previous = null;

		for (ServedStopDTO stop : stops) {
			LineStop current = saveServedStop(date, trainId, stop, previous);
			
			result.add(current);

			// -- We are making the link to keep trace of direction
			previous = current;
		}

		return result;
	}

	private LineStop createUpdateOrRetrieve(LineStop lineStop) {
		Train train = saveOrRetrieveTrain(lineStop.getTrain());
		Station station = saveOrRetrieveStation(lineStop.getStation());
		LineStop result = lineStopDao.findByTrainAndDateAndStation(
				train, lineStop.getDate(), station);

		if (result != null) {
			log.debug("We update a LineStop={}.", result);

			mapper.map(lineStop, result);
		} else {
			log.debug("We create a new LineStop.");

			result = lineStop;
		}

		return result;
	}

	private RailtimeTrain saveOrRetrieveRailtimeTrain(RailtimeTrain train) {
		RailtimeTrain result = null;
		RailtimeTrain persistedTrain = railtimeTrainDao.findByRailtimeId(train
				.getRailtimeId());

		if (persistedTrain == null) {
			result = railtimeTrainDao.save(train);
		} else {
			result = persistedTrain;
		}

		return result;
	}

	private Train saveOrRetrieveTrain(Train train) {
		Train result = trainDao.findByEnglishName(train
				.getEnglishName());

		if (result == null) {
			result = trainDao.save(train);
		} 

		return result;
	}

	private Station saveOrRetrieveStation(Station station) {
		Station result = stationDao.findByEnglishName(station.getEnglishName());

		if (result == null) {
			result = stationDao.save(new Station(station.getEnglishName()));
		}

		return result;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

}
