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
						"SELECT o FROM LineStop o "
								+ "WHERE o.station.englishName = :stationName "
								+ "AND o.date = :date "
								+ "AND o.departureTime.delay IS NOT NULL "
								+ "AND o.departureTime.delay >= :delayThreshold ")
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
						"SELECT o FROM LineStop o "
								+ "WHERE o.station.englishName = :stationName "
								+ "AND o.date = :date "
								+ "AND o.arrivalTime.delay IS NOT NULL "
								+ "AND o.arrivalTime.delay >= :delayThreshold ")
				.setParameter("delayThreshold", new Long(delayThreshold))
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("stationName", station.getEnglishName())
				.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> findAllUniqueDates() {
		return (List<Date>) entityManager
				.createQuery("SELECT DISTINCT o.date FROM LineStop o ")
				.getResultList();
	}

}
