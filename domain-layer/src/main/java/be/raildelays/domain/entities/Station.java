package be.raildelays.domain.entities;

import be.raildelays.domain.Language;

import javax.persistence.*;

/**
 * Immutable entity defining a train station.
 * <p/>
 * Unicity of a train is done on the English name.
 *
 * @author Almex
 * @see AbstractEntity
 */
@Entity
@Table(name = "STATION")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Station extends AbstractI18nEntity {

    private static final long serialVersionUID = -3436298381031779337L;

    protected Station() {
        super();
    }

    public Station(String englishName) {
        super(englishName);
    }

    public Station(String name, Language language) {
        super(name, language);
    }

    public Station(String englishName, String dutchName, String frenchName) {
        super(englishName, dutchName, frenchName);
    }

    @Override
    public String toString() {
        return new StringBuilder("Station: ") //
                .append("{ ") //
                .append("id: ").append(getId()).append(", ") //
                .append("dutchName: ").append(getDutchName()).append(", ") //
                .append("englishName: ").append(getEnglishName()).append(", ") //
                .append("frenchName: ").append(getFrenchName()) //
                .append(" }").toString();
    }

}
