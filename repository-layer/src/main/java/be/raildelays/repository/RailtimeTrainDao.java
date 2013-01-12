package be.raildelays.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import be.raildelays.domain.entities.RailtimeTrain;

/**
 * Repository that manage storing a {@link RailtimeTrain}.
 * 
 * @author Almex
 */
public interface RailtimeTrainDao extends JpaRepository<RailtimeTrain, String> {
	
	/**
	 * Search for a RailtimeTrain by its English name.
	 * 
	 * @param name strict name that should match to find a RailtimeTrain.
	 * @param language in which you want to do the search.
	 * @return a {@link RailtimeTrain}
	 */
	public RailtimeTrain findByEnglishName(String englishName);
	
	
	/**
	 * Search for a RailtimeTrain by its French name.
	 * 
	 * @param name strict name that should match to find a RailtimeTrain.
	 * @param language in which you want to do the search.
	 * @return a {@link RailtimeTrain}
	 */
	public RailtimeTrain findByFrenchName(String frenchName);
	
	
	/**
	 * Search for a RailtimeTrain by its Dutch name.
	 * 
	 * @param name strict name that should match to find a RailtimeTrain.
	 * @param language in which you want to do the search.
	 * @return a {@link RailtimeTrain}
	 */
	public RailtimeTrain findByDutchName(String dutchName);
	
}
