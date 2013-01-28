package be.raildelays.repository.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDaoCustom;

public class LineStopDaoCustomJpa implements LineStopDaoCustom {

	@PersistenceContext(unitName = "raildelays-repository")
	private EntityManager entityManager;

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
						"Select o From LineStop o "
								+ "Where o.station.englishName = :stationName "
								+ "And o.date = :date "
								+ "And o.arrivalTime.delay Is Not Null "
								+ "And o.arrivalTime.delay >= :delayThreshold ")
				.setParameter("delayThreshold", new Long(delayThreshold))
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("stationName", station.getEnglishName())
				.getResultList();

	}

}
