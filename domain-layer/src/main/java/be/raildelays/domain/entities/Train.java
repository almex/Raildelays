package be.raildelays.domain.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Immutable entity defining a train. 
 * Unicity of a train is done on the English name.
 * 
 * @author Almex
 * @see Entity
 */
@Entity
@Table(name = "TRAIN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Train implements Serializable {

	private static final long serialVersionUID = -1527666012499664304L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected final Long id;

	@Column(updatable = false, unique = true)
	@NotNull
	protected final String englishName;

	protected final String frenchName;

	protected final String dutchName;

	protected Train() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	@SuppressWarnings("unused")
	// Already implemented for future use
	private Train(Train train) {
		this();
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param name English name for this train
	 */
	public Train(String name) {
		this.id = null;
		this.englishName = name;
		this.dutchName = "";
		this.frenchName = "";
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(englishName) //
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;

		if (obj == this) {
			result = true;
		} else {
			if (obj instanceof Train) {
				Train train = (Train) obj;

				result = new EqualsBuilder().append(englishName,
						train.englishName).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	public String toString() {
		return new StringBuilder("Train: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
				.append("dutchName: ").append(dutchName).append(", ") //
				.append("englishName: ").append(englishName).append(", ") //
				.append("frenchName: ").append(frenchName) //
				.append(" }").toString();
	}

	public Long getId() {
		return id;
	}

	public String getEnglishName() {
		return englishName;
	}

	public String getFrenchName() {
		return frenchName;
	}

	public String getDutchName() {
		return dutchName;
	}
}
