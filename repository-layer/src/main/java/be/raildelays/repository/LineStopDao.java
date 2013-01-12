package be.raildelays.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import be.raildelays.domain.entities.LineStop;

/**
 * Repository that manage storing a {@link LineStop}.
 * 
 * @author Almex
 */
public interface LineStopDao extends JpaRepository<LineStop, Long>, LineStopDaoCustom {

}
