package be.raildelays.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import be.raildelays.domain.entities.Station;

/**
 * Repository that manage storing a {@link Station}.
 * 
 * @author Almex
 */
public interface StationDao extends JpaRepository<Station, Long> {
	
	/**
	 * Search for a Station by its English name.
	 * 
	 * @param name strict name that should match to find a Station.
	 * @param language in which you want to do the search.
	 * @return a {@link Station}
	 */
    @Query("select o from Station o where o.englishName = ?1")
	public Station findByEnglishName(String englishName);
	
	
	/**
	 * Search for a Station by its French name.
	 * 
	 * @param name strict name that should match to find a Station.
	 * @param language in which you want to do the search.
	 * @return a {@link Station}
	 */
	public Station findByFrenchName(String frenchName);
	
	
	/**
	 * Search for a Station by its Dutch name.
	 * 
	 * @param name strict name that should match to find a Station.
	 * @param language in which you want to do the search.
	 * @return a {@link Station}
	 */
	public Station findByDutchName(String dutchName);
}
