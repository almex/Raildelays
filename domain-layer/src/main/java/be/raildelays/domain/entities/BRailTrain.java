package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class BRailTrain extends Train {

	private static final long serialVersionUID = 7844213206211119783L;
	
	@Column(unique=true)
	private String bRailId;
	
	public BRailTrain() {
		bRailId = "";
	}
	
	public BRailTrain(String name, String bRailId) {
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
