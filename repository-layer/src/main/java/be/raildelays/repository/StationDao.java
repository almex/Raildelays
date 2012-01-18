package be.raildelays.repository;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Station;

/**
 * Repository that manage storing a {@link Station}.
 * 
 * @author Almex
 */
public interface StationDao {
	
	/**
	 * Persist a new Station to the repository.
	 * 
	 * @param station entity to persist.
	 * @return the same entity with its id filled-in
	 */
	public Station createStation(Station station);
	
	/**
	 * Persist a new Station to the repository, if it exists it will update it.
	 * 
	 * @param englishName English name for this station.
	 * @return the same entity with its id filled-in
	 */
	public Station createOrRetrieveStation(String englishName);
	
	/**
	 * Search for a Station by its name.
	 * 
	 * @param name strict name that should match to find a Station.
	 * @param language in which you want to do the search.
	 * @return a {@link Station}
	 */
	public Station retrieveStation(String name, Language language);
	
	/**
	 * Search for a Station by its English name.
	 * 
	 * @param name strict name that should match to find a Station.
	 * @param language in which you want to do the search.
	 * @return a {@link Station}
	 */
	public Station retrieveStation(String name);
	
	/**
	 * Remove a Station.
	 * 
	 * @param idStation Station id
	 */
	public void deleteStation(Long idStation);
	
	/**
	 * Update a Station.
	 * 
	 * @param station that should contain at least an id.
	 * @return the persisted version of the Station after updating.
	 */
	public Station updateStation(Station station);
}
