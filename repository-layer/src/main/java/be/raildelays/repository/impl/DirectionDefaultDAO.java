package be.raildelays.repository.impl;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import be.raildelays.domain.entities.LineStop;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.LineStopDAO;

@Repository(value = "LineStopDAO")
public class DirectionDefaultDAO implements LineStopDAO {

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
	public List<LineStop> searchLineStop(Station departure, Station arrival,
			Date date) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeLineStop(Long idLineStop) {
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
