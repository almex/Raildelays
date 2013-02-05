package be.raildelays.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Immutable entity of a {@link Train} specific to Railtime portal.
 * 
 * @author Almex
 * @see Train
 */
@Entity
public class RailtimeTrain extends Train {

	private static final long serialVersionUID = -7755979419472957633L;

	@Column(unique = true)
	private final String railtimeId;

	@SuppressWarnings("unused")
	private RailtimeTrain() {
		super();
		this.railtimeId = "";
	}

	public RailtimeTrain(final String name, final String railtimeId) {
		super(name);
		this.railtimeId = railtimeId;
	}

	@Override
	public String toString() {
		return new StringBuilder("RailtimeTrain: ") //
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(railtimeId) //
				.toHashCode();
	}

	public String getRailtimeId() {
		return railtimeId;
	}

}
