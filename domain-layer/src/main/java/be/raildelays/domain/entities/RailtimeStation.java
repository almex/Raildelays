package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Station specific to Railtime portal. 
 * 
 * @author Almex
 * @see Station
 */
@Entity
public class RailtimeStation extends Station {

	private static final long serialVersionUID = -8940001829880027858L;

	@Column(unique=true)
	private final String railtimeId;
	
	@SuppressWarnings("unused")
	private RailtimeStation() {
		super();
		this.railtimeId = "";
	}
	
	public RailtimeStation(final String name, final String railtimeId) {
		super(name);
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
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(railtimeId) //
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder("RailtimeStation: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("railtimeId: ").append(railtimeId).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}

	public String getRailtimeId() {
		return railtimeId;
	}
}
