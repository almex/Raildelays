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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Entity representing a train station.
 * 
 * Uniqueness is based on the English name (i.e. : business key).
 * 
 * @author Almex
 */
@Entity
@Table(name = "STATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Station implements Serializable {

	private static final long serialVersionUID = -3436298381031779337L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	protected Long id;

	@Column(nullable = false, updatable = false, unique = true)
	protected String englishName;

	protected String frenchName;

	protected String dutchName;

	protected Station() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	public Station(String name) {
		this();
		this.englishName = name;
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
						station.getEnglishName()).isEquals();
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

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getFrenchName() {
		return frenchName;
	}

	public void setFrenchName(String frenchName) {
		this.frenchName = frenchName;
	}

	public String getDutchName() {
		return dutchName;
	}

	public void setDutchName(String dutchName) {
		this.dutchName = dutchName;
	}

}
