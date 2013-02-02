package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Station specific to BRail portal. 
 * 
 * @author Almex
 */
@Entity
public class BRailStation extends Station {

	private static final long serialVersionUID = -5603009982357954186L;
	
	@Column(unique=true)
	private final String bRailId;
	
	@SuppressWarnings("unused")
	private BRailStation() {
		bRailId = "";
	}
	
	public BRailStation(final String name, final String bRailId) {
		super(name);
		this.bRailId = bRailId;
	}

	
	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof BRailStation) {
				BRailStation station = (BRailStation) obj;

				result = new EqualsBuilder().append(bRailId,
						station.getbRailId()).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(bRailId) //
				.toHashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder("BRailStation: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("railtimeId: ").append(bRailId).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}

	public String getbRailId() {
		return bRailId;
	}
}
