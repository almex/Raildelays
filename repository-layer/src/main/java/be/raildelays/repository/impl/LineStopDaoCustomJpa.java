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
import be.raildelays.repository.LineStopDaoCustom;

@Repository(value = "lineStopDao")
public class LineStopDaoCustomJpa implements LineStopDaoCustom {

	@PersistenceContext(unitName="raildelays-repository")
	private EntityManager entityManager;


	private Logger logger = Logger.getLogger(LineStopDaoCustomJpa.class);

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<LineStop> findByTrain(String idTrain, Date date) {
		Date from = DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
		Date to = DateUtils.addMilliseconds(DateUtils.addDays(from, 1),-1);
		
		logger.debug("from="+from+" - to="+to);
		
		return (List<LineStop>) entityManager.createQuery("select o from LineStop o where o.train.railtimeId = :idTrain and o.departureTime.expected >= :departure and o.departureTime.expected <= :arrival")
				.setParameter("idTrain", idTrain)
				.setParameter("departure", from, TemporalType.TIMESTAMP)
				.setParameter("arrival", to, TemporalType.TIMESTAMP)
				.getResultList();
		
	}

}
