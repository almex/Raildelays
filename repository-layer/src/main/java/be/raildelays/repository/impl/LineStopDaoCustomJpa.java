package be.raildelays.repository.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDaoCustom;

@Repository(value = "lineStopDao")
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

	@PersistenceContext(unitName = "raildelays-repository")
	private EntityManager entityManager;

	private Logger logger = Logger.getLogger(LineStopDaoCustomJpa.class);

//	/**
//	 * {@inheritDoc}
//	 */
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<LineStop> findByTrain(String idTrain, Date date) {
//		return (List<LineStop>) entityManager
//				.createQuery(
//						"select o from LineStop o "
//								+ "where o.train.railtimeId = :idTrain "
//								+ "and o.date = :date ")
//				.setParameter("idTrain", idTrain)
//				.setParameter("date", date, TemporalType.DATE)
//				.getResultList();
//
//	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LineStop> findDepartureDelays(Date date, Station station,
			int delayThreshold) {
		return (List<LineStop>) entityManager
				.createQuery(
						"select o from LineStop o "
								+ "where o.station.englishName = :stationName "
								+ "and o.date = :date "
								+ "and o.departureTime.delay is not null "
								+ "and o.departureTime.delay >= :delayThreshold ")
				.setParameter("delayThreshold", new Long(delayThreshold))
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("stationName", station.getEnglishName())
				.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LineStop> findArrivalDelays(Date date, Station station,
			int delayThreshold) {
		return (List<LineStop>) entityManager
				.createQuery(
						"select o from LineStop o "
								+ "where o.station.englishName = :stationName "
								+ "and o.date = :date "
								+ "and o.arrivalTime.delay is not null "
								+ "and o.arrivalTime.delay >= :delayThreshold ")
				.setParameter("delayThreshold", new Long(delayThreshold))
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("stationName", station.getEnglishName())
				.getResultList();

	}

}
