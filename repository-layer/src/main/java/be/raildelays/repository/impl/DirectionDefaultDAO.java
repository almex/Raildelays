package be.raildelays.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import be.raildelays.repository.DirectionDAO;

@Repository(value = "DirectionDAO")
public class DirectionDefaultDAO implements DirectionDAO {

	@PersistenceContext
	private EntityManager entityManager;

}
