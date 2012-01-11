package be.raildelays.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import be.raildelays.domain.railtime.Train;

/**
 * 
 * @author Almex
 */
@Entity
@Table(name="DIRECTION")
public class Direction extends be.raildelays.domain.railtime.Direction {

	private static final long serialVersionUID = 7142886242889314414L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	public Direction(Train train) {
		super(train);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
