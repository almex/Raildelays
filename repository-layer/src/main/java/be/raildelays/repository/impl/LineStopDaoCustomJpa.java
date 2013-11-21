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
				.setParameter("delayThreshold", Long.valueOf(delayThreshold))
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
								+ "AND (o.arrivalTime.delay >= :delayThreshold OR o.canceled = true)")
				.setParameter("delayThreshold", Long.valueOf(delayThreshold))
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("stationName", station.getEnglishName())
				.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> findAllUniqueDates(Date after, Date before) {
		return (List<Date>) entityManager
				.createQuery(
						"SELECT DISTINCT o.date " 
								+ "FROM LineStop o "
								+ "WHERE o.date >= :after "
								+ "AND o.date <= :before "
								+ "ORDER BY o.date ASC")
				.setParameter("after", after)
				.setParameter("before", before)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Date> findAllUniqueDates() {
		return (List<Date>) entityManager
				.createQuery(
						"SELECT DISTINCT o.date " 
								+ "FROM LineStop o "
								+ "ORDER BY o.date ASC")
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Date> findAllUniqueDates(Date lastDate) {
		return (List<Date>) entityManager
				.createQuery(
						"SELECT DISTINCT o.date " 
								+ "FROM LineStop o "
								+ "WHERE o.date <= :before "
								+ "ORDER BY o.date ASC")
				.setParameter("before", lastDate)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LineStop> findNextExpectedArrivalTime(Station station, Date date) {
		return (List<LineStop>) entityManager
				.createQuery(
						"SELECT DISTINCT o " 
								+ "FROM LineStop o "
								+ "WHERE o.station.englishName = :stationName "
								+ "AND o.date = :date "
								+ "AND o.arrivalTime IS NOT NULL "
								+ "AND o.arrivalTime.expected > :time "
								+ "ORDER BY o.arrivalTime.expected ASC")
				.setParameter("stationName", station.getEnglishName())
				.setParameter("date", date, TemporalType.DATE)
				.setParameter("time", date, TemporalType.TIME)
				.getResultList();
	}

}
