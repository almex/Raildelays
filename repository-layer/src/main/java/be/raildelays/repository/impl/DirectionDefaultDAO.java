package be.raildelays.repository.impl;

import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import be.raildelays.domain.entities.Direction;
import be.raildelays.domain.railtime.Station;
import be.raildelays.repository.DirectionDAO;

@Repository(value = "DirectionDAO")
public class DirectionDefaultDAO implements DirectionDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Direction createDirection(Direction direction) {
		entityManager.persist(direction);
		return direction;
	}

	@Override
	public Direction searchDirection(Station from, Station to, Date date) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeDirection(Long idDirection) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	@Override
	public Direction updateDirection(Direction direction) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
