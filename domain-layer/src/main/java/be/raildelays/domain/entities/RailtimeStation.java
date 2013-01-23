package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;

@Entity
public class RailtimeStation extends Station {

	private static final long serialVersionUID = -8940001829880027858L;

	@Column(unique=true)
	private String railtimeId;
	
	public RailtimeStation() {
		railtimeId = "";
	}

	public RailtimeStation(String railtimeId) {
		super(railtimeId);
		this.railtimeId = railtimeId;
	}
	
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
	
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof RailtimeStation) {
				RailtimeStation station = (RailtimeStation) obj;

				result = new EqualsBuilder().append(railtimeId,
						station.getRailtimeId()).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}
	
	@Override
	public String toString() {
		return new StringBuilder("Station: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("railtimeId: ").append(railtimeId).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}
}
