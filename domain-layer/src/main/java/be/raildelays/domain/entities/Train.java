package be.raildelays.domain.entities;

import be.raildelays.domain.Language;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 * Entity defining a train.
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
        super();
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
