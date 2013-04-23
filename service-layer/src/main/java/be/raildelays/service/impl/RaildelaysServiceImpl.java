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
import be.raildelays.domain.entities.TimestampDelay;
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

	private static final Logger LOGGER = LoggerFactory
			.getLogger(RaildelaysServiceImpl.class);

	@Override
	@Transactional
	public List<LineStop> saveRouteLog(final RouteLogDTO routeLog) {
		LOGGER.debug("Saving route log for train={} and date={}...",
				routeLog.getTrainId(), routeLog.getDate());

		// -- Validate our inputs
		validator.validate(routeLog);

		return persist(routeLog.getDate(), routeLog.getTrainId(),
				routeLog.getStops());
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

	private LineStop saveServedStop(final Date date, final String trainId,
			final ServedStopDTO stop, final LineStop previous) {

		LOGGER.debug(
				"Saving timetable for train={}, date={} and stop={}...",
				new Object[] { trainId, date, stop });

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
		TimestampDelay arrivalTime = new TimestampDelay(stop.getArrivalTime(),
				stop.getArrivalDelay());
		TimestampDelay departureTime = new TimestampDelay(
				stop.getDepartureTime(), stop.getDepartureDelay());
		LineStop lineStop = new LineStop(date, persistedTrain,
				persistedStation, arrivalTime, departureTime,
				stop.isCanceled(), previous);

		// -- Validate our output
		validator.validate(lineStop);

		return lineStopDao.save(createOrUpdate(lineStop));
	}

	private LineStop createOrUpdate(LineStop lineStop) {
		LineStop result = null;
		Train train = saveOrRetrieveTrain(lineStop.getTrain());
		Station station = saveOrRetrieveStation(lineStop.getStation());
		LineStop persistedLineStop = lineStopDao.findByTrainAndDateAndStation(train,
				lineStop.getDate(), station);

		if (persistedLineStop != null) {
			LOGGER.debug("We update a LineStop={}.", persistedLineStop);

			mapper.map(lineStop, persistedLineStop);
			result = persistedLineStop;
		} else {
			LOGGER.debug("We create a new LineStop.");

			result = lineStop;
		}

		return result;
	}

	@Override
	@Transactional
	public List<LineStop> searchDelaysBetween(Date date, Station stationA,
			Station stationB, int delayThreshold) {
		List<LineStop> result = new ArrayList<LineStop>();

		LOGGER.info(
				"Searching line stops for date={} stationA={} stationB={} delayThreshold={}",
				new Object[] { date, stationA, stationB, delayThreshold });

		result.addAll(lineStopDao.findArrivalDelays(date, stationA,
				delayThreshold));

		LOGGER.debug("Retrieved {} line stops from station A", result.size());

		result.addAll(lineStopDao.findArrivalDelays(date, stationB,
				delayThreshold));

		LOGGER.debug("Retrieved {} line stops from station A and station B",
				result.size());

		LOGGER.trace("Retrieved line stops =", result.toString());

		return result;
	}

	@Override
	public List<Date> searchAllDates(Date from, Date to) {
		List<Date> result = null;

		if (from == null && to == null) {
			result = lineStopDao.findAllUniqueDates();
		} else {
			result = lineStopDao.findAllUniqueDates(from, to);
		}

		return result;
	}

	@Override
	public List<Date> searchAllDates(Date lastDate) {
		List<Date> result = null;

		if (lastDate == null) {
			result = lineStopDao.findAllUniqueDates();
		} else {
			result = lineStopDao.findAllUniqueDates(lastDate);
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
		Train result = trainDao.findByEnglishName(train.getEnglishName());

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
