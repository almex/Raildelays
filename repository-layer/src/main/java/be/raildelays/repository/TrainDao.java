package be.raildelays.repository;

import be.raildelays.domain.entities.Train;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository that manage storing a {@link Train}.
 *
 * @author Almex
 */
public interface TrainDao extends JpaRepository<Train, Long> {

    /**
     * Search for a Train by its English name.
     *
     * @param name     strict name that should match to find a Train.
     * @param language in which you want to do the search.
     * @return a {@link Train}
     */
    public Train findByEnglishName(String englishName);


    /**
     * Search for a Train by its French name.
     *
     * @param name     strict name that should match to find a Train.
     * @param language in which you want to do the search.
     * @return a {@link Train}
     */
    public Train findByFrenchName(String frenchName);


    /**
     * Search for a Train by its Dutch name.
     *
     * @param name     strict name that should match to find a Train.
     * @param language in which you want to do the search.
     * @return a {@link Train}
     */
    public Train findByDutchName(String dutchName);

}
