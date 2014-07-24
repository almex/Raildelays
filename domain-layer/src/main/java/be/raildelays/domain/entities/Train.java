package be.raildelays.domain.entities;

import be.raildelays.domain.Language;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
public class Train implements Serializable, Comparable<Train> {

	private static final long serialVersionUID = -1527666012499664304L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected final Long id;

	@Column(name = "ENGLISH_NAME", updatable = false, unique = true)
	@NotNull
	protected final String englishName;

	@Column(name = "FRENCH_NAME")
	protected final String frenchName;

	@Column(name = "DUTCH_NAME")
	protected final String dutchName;

	/**
	 * Default constructor.
	 */
	protected Train() {
		this.id = null;
		this.englishName = "";
		this.dutchName = "";
		this.frenchName = "";
	}

	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train
	 */
	public Train(final String englishName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = "";
		this.frenchName = "";
	}

    /**
     * Initialization constructor.
     *
     * @param name for this train station.
     */
    public Train(final String name, Language language) {
        this.id = null;

        switch (language) {
            case EN:
                this.englishName = name;
                this.dutchName = "";
                this.frenchName = "";
                break;
            case NL:
                this.englishName = "";
                this.dutchName = name;
                this.frenchName = "";
                break;
            case FR:
                this.englishName = "";
                this.dutchName = "";
                this.frenchName = name;
                break;
            default:
                this.englishName = "";
                this.dutchName = "";
                this.frenchName = "";
        }
    }

	/**
	 * Initialization constructor.
	 * 
	 * @param englishName English name for this train
	 * @param dutchName Dutch name for this train
	 * @param frenchName French name for this train
	 */
	public Train(final String englishName, final String dutchName, final String frenchName) {
		this.id = null;
		this.englishName = englishName;
		this.dutchName = dutchName;
		this.frenchName = frenchName;
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

    @Override
    public int compareTo(Train train) {
        int result = 0;

        if (train == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(englishName, train.getEnglishName())
                    .toComparison();
        }

        return result;
    }
}
