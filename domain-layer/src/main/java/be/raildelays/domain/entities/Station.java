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
 * Immutable entity defining a train station.
 * 
 * Unicity of a train is done on the English name.
 * 
 * @author Almex
 * @see Entity
 */
@Entity
@Table(name = "STATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Station implements Serializable, Cloneable {

	private static final long serialVersionUID = -3436298381031779337L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected final Long id;

	@Column(updatable = false, unique = true)
	@NotNull
	protected final String englishName;

	protected final String frenchName;

	protected final String dutchName;

	/**
	 * Default contrcutor.
	 */
	protected Station() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train station.
	 */
	public Station(final String englishName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = "";
		this.frenchName = "";
	}
	
	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train
	 * @param dutchName Dutch name for this train
	 * @param frenchName French name for this train
	 */
	public Station(final String englishName, final String dutchName, final String frenchName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = dutchName;
		this.frenchName = frenchName;
	}

	@Override
	public String toString() {
		return new StringBuilder("Station: ") //
				.append("{ ") //
				.append("id: ").append(id).append(", ") //
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
			if (obj instanceof Station) {
				Station station = (Station) obj;

				result = new EqualsBuilder().append(englishName,
						station.englishName).isEquals();
			} else {
				result = false;
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(englishName) //
				.toHashCode();
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
