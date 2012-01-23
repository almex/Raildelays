package be.raildelays.repository.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Repository;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDao;

@Repository(value = "lineStopDao")
public class LineStopJpaDao implements LineStopDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineStop createLineStop(LineStop lineStop) {
		entityManager.persist(lineStop);

		return lineStop;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LineStop> retrieveLineStop(Station departure, Station arrival,
			Date date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LineStop> retrieveLineStop(String idTrain, Date date) {
		Date from = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date to = DateUtils.addMilliseconds(DateUtils.addDays(from, 1),-1);
		
		return (List<LineStop>) entityManager.createQuery("select o from LineStop o where o.train.railtimeId = :idTrain and o.departureTime.expected >= :departure and o.departureTime.expected <= :arrival")
				.setParameter("idTrain", idTrain)
				.setParameter("departure", from, TemporalType.TIMESTAMP)
				.setParameter("arrival", to, TemporalType.TIMESTAMP)
				.getResultList();
		/*return (List<LineStop>) entityManager.createQuery("select o from LineStop o where  o.departureTime.expected >= :from and o.departureTime.expected <= :to")
				.setParameter("from", from, TemporalType.TIMESTAMP)
				.setParameter("to", to, TemporalType.TIMESTAMP)
				.getResultList();*/
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteLineStop(Long idLineStop) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LineStop updateLineStop(LineStop lineStop) {
		return entityManager.merge(lineStop);
	}

}
