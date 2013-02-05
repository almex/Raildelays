package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Immutable entity defining a {@link Train} specific to BRail portal.
 * 
 * @author Almex
 * @see Entity
 */
@Entity
public class BRailTrain extends Train {

	private static final long serialVersionUID = 7844213206211119783L;

	@Column(unique = true)
	private final String bRailId;

	@SuppressWarnings("unused")
	// Already implemented for a future usage
	private BRailTrain() {
		bRailId = "";
	}

	/**
	 * 
	 * @param name
	 * @param bRailId
	 */
	public BRailTrain(final String name, final String bRailId) {
		super(name);
		this.bRailId = bRailId;
	}

	@Override
	public String toString() {
		return new StringBuilder("BRailTrain: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("railtimeId: ").append(bRailId).append(", ") //
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
			if (obj instanceof BRailTrain) {
				BRailTrain train = (BRailTrain) obj;

				result = new EqualsBuilder()
						.append(bRailId, train.getbRailId()).isEquals();
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

	public String getbRailId() {
		return bRailId;
	}

}
