package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class BRailStation extends Station {

	private static final long serialVersionUID = -5603009982357954186L;
	
	@Column(unique=true)
	private String bRailId;
	
	public BRailStation() {
		bRailId = "";
	}
	
	public BRailStation(String name, String bRailId) {
		super(name);
		this.bRailId = bRailId;
	}

	public String getbRailId() {
		return bRailId;
	}

	public void setbRailId(String bRailId) {
		this.bRailId = bRailId;
	}
}
