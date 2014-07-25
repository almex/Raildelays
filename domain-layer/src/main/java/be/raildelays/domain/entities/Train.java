package be.raildelays.domain.entities;

import be.raildelays.domain.Language;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Immutable entity defining a train. 
 * Unicity of a train is done on the English name.
 * 
 * @author Almex
 * @see AbstractEntity
 */
@Entity
@Table(name = "TRAIN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Train extends AbstractI18nEntity {

	private static final long serialVersionUID = -1527666012499664304L;

    protected Train() {

    }

    public Train(String englishName) {
        super(englishName);
    }

    public Train(String name, Language language) {
        super(name, language);
    }

    public Train(String englishName, String dutchName, String frenchName) {
        super(englishName, dutchName, frenchName);
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
}
