package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class RailtimeStation extends Station {

	private static final long serialVersionUID = -8940001829880027858L;

	@Column(unique=true)
	private String railtimeId;

	public RailtimeStation(String name, String railtimeId) {
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
