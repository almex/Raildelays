package be.raildelays.repository;

import java.sql.Date;

import be.raildelays.domain.entities.Direction;
import be.raildelays.domain.railtime.Station;

/**
 * Repository that manage storing a Direction.
 * 
 * @author Almex
 */
public interface DirectionDAO {

	public Direction createDirection(Direction direction);
	
	public Direction searchDirection(Station from, Station to, Date date);
	
	public void removeDirection(Long idDirection);
	
	public Direction updateDirection(Direction direction);
}
