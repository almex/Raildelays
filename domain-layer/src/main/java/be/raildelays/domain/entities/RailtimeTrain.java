package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;

@Entity
public class RailtimeTrain extends Train {

	private static final long serialVersionUID = -7755979419472957633L;
	
	@Column(unique=true)	
	private String railtimeId;
	
	public RailtimeTrain() {
		railtimeId = "";
	}
	
	public RailtimeTrain(String name, String railtimeId) {
		super(name);
		this.railtimeId = railtimeId;
	}

	public RailtimeTrain(String railtimeId) {
		super(railtimeId);
		this.railtimeId = railtimeId;		
	}

	public String getRailtimeId() {
		return railtimeId;
	}

	public void setRailtimeId(String railtimeId) {
		this.railtimeId = railtimeId;
	}

	@Override
	public String toString() {
		return new StringBuilder("Train: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("railtimeId: ").append(railtimeId).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof RailtimeTrain) {
				RailtimeTrain train = (RailtimeTrain) obj;

				result = new EqualsBuilder().append(railtimeId,
						train.getRailtimeId()).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

}
