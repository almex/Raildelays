package be.raildelays.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;
import be.raildelays.repository.StationDao;

@Repository(value = "stationDao")
public class StationJpaDao implements StationDao {

	@PersistenceContext(unitName="raildelays-repository")
	private EntityManager entityManager;
	


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Station createStation(Station station) {
		entityManager.persist(station);
		
		return station;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Station createOrRetrieveStation(String englishName) {
		Station result = null;
		
		try {
			result = retrieveStation(englishName);
		} catch (NoResultException e) {
			result = createStation(new Station(englishName));
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Station retrieveStation(String name, Language language) {
		Station result = null;		
		
		switch(language) {
		
		case ENGLISH:
			result = (Station) entityManager.createQuery("select o from Station o where o.englishName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		case FRENCH:
			result = (Station) entityManager.createQuery("select o from Station o where o.frenchName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		case DUTCH:
			result = (Station) entityManager.createQuery("select o from Station o where o.dutchName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		default:
			result = null;			
		}
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Station retrieveStation(String name) {
		return retrieveStation(name, Language.ENGLISH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteStation(Long idStation) {
		throw new UnsupportedOperationException();		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Station updateStation(Station station) {
		return entityManager.merge(station);
	}

}
