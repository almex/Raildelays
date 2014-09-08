package be.raildelays.repository;

import be.raildelays.domain.entities.RailtimeTrain;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository that manage storing a {@link RailtimeTrain}.
 *
 * @author Almex
 */
public interface RailtimeTrainDao extends JpaRepository<RailtimeTrain, String> {

    /**
     * Search for a RailtimeTrain by its English name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByEnglishName(String englishName);


    /**
     * Search for a RailtimeTrain by its French name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByFrenchName(String frenchName);


    /**
     * Search for a RailtimeTrain by its Dutch name.
     *
     * @param name     strict name that should match to find a RailtimeTrain.
     * @param language in which you want to do the search.
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByDutchName(String dutchName);


    /**
     * Search for a RailtimeTrain by its Id coming from Railtime.
     *
     * @param railtimeId Id coming from Railtime
     * @return a {@link RailtimeTrain} if found, <code>null</code> otherwise.
     */
    RailtimeTrain findByRailtimeId(String railtimeId);
}
