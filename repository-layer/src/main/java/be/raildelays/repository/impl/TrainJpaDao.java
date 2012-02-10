package be.raildelays.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.Train;
import be.raildelays.repository.TrainDao;

@Repository(value = "trainDao")
public class TrainJpaDao implements TrainDao {

	@PersistenceContext(unitName="raildelays-repository")
	private EntityManager entityManager;
	

	private Logger logger = Logger.getLogger(TrainJpaDao.class);
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Train createTrain(Train train) {
		entityManager.persist(train);
		
		return train;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RailtimeTrain createRailtimeTrain(RailtimeTrain train) {
		entityManager.persist(train);
		
		return train;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RailtimeTrain createOrRetrieveRailtimeTrain(RailtimeTrain train) {
		RailtimeTrain result = null;
		
		try {
			result = retrieveRailtimeTrain(train.getRailtimeId());
			logger.debug("retrieveTrain: already exists "+result);
		} catch (NoResultException e) {
			result = createRailtimeTrain(train);
		}
		
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Train retrieveTrain(String name, Language language) {
		Train result = null;		
		
		switch(language) {
		
		case ENGLISH:
			result = (Train) entityManager.createQuery("select o from Train o where o.englishName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		case FRENCH:
			result = (Train) entityManager.createQuery("select o from Train o where o.frenchName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		case DUTCH:
			result = (Train) entityManager.createQuery("select o from Train o where o.dutchName = :name")
											.setParameter("name", name)
											.getSingleResult();
			break;
		default:
			result = null;			
		}
		
		logger.debug("retrieveTrain="+result);
		
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public RailtimeTrain retrieveRailtimeTrain(String idRailtime) {
		return (RailtimeTrain) entityManager.createQuery("select o from RailtimeTrain o where o.railtimeId = :id")
											.setParameter("id", idRailtime)
											.getSingleResult();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTrain(Long idTrain) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Train updateTrain(Train train) {
		return entityManager.merge(train);
	}

}
