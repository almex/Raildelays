package be.raildelays.repository;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.RailtimeTrain;
import be.raildelays.domain.entities.Train;

/**
 * Repository that manage storing a {@link Train}.
 * 
 * @author Almex
 */
public interface TrainDao {
	
	/**
	 * Persist a new {@link Train} to the repository.
	 * 
	 * @param train entity to persist.
	 * @return the same entity with its id filled-in
	 */
	public Train createTrain(Train train);
	
	/**
	 * Persist a new {@link RailtimeTrain} to the repository.
	 * 
	 * @param train entity to persist.
	 * @return the same entity with its id filled-in
	 */
	public RailtimeTrain createRailtimeTrain(RailtimeTrain train);
	
	/**
	 * Persist a new {@link RailtimeTrain} to the repository.
	 * 
	 * @param idRailtime entity to persist.
	 * @return the same entity with its id filled-in
	 */
	public RailtimeTrain createOrRetrieveRailtimeTrain(RailtimeTrain train);
	
	/**
	 * Search for a train by its name.
	 * 
	 * @param name strict name that should match to find a train.
	 * @param language in which you want to do the search.
	 * @return a {@link Train}
	 */
	public Train retrieveTrain(String name, Language language);
	
	/**
	 * Search for a train by its Railtime id.
	 * 
	 * @param idRailtime train's id in its Railtime format.
	 * @return a {@link RailtimeTrain}
	 */
	public RailtimeTrain retrieveRailtimeTrain(String idRailtime);
	
	/**
	 * Remove a train.
	 * 
	 * @param idTrain train id
	 */
	public void deleteTrain(Long idTrain);
	
	/**
	 * Update a train.
	 * 
	 * @param train that should contain at least an id.
	 * @return the persisted version of the train after updating.
	 */
	public Train updateTrain(Train train);
}
