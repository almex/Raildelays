package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RailtimeTrain extends Train {

	private static final long serialVersionUID = -7755979419472957633L;
	
	@Column(unique=true)
	private String railtimeId;
	
	public RailtimeTrain(String name, String railtimeId) {
		super(name);
		this.railtimeId = railtimeId;
	}

	public String getRailtimeId() {
		return railtimeId;
	}

	public void setRailtimeId(String railtimeId) {
		this.railtimeId = railtimeId;
	}

}
