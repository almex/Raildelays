package be.raildelays.domain.entities;

import be.raildelays.domain.Language;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Almex
 */
@MappedSuperclass
public class AbstractI18nEntity extends AbstractEntity implements Serializable, Cloneable, Comparable<AbstractI18nEntity> {

    @Column(name = "ENGLISH_NAME")
    protected String englishName;
    @Column(name = "FRENCH_NAME")
    protected String frenchName;
    @Column(name = "DUTCH_NAME")
    protected String dutchName;

    /**
     * Default contrcutor.
     */
    protected AbstractI18nEntity() {
        this.englishName = "";
        this.dutchName = "";
        this.frenchName = "";
    }

    /**
     * Initialization constructor.
     *
     * @param englishName English name for this train station.
     */
    public AbstractI18nEntity(final String englishName) {
        this(englishName, "", "");
    }

    /**
     * Initialization constructor.
     *
     * @param name for this train station.
     */
    public AbstractI18nEntity(final String name, Language language) {
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
     * @param dutchName   Dutch name for this train
     * @param frenchName  French name for this train
     */
    public AbstractI18nEntity(final String englishName, final String dutchName, final String frenchName) {
        this.englishName = englishName;
        this.dutchName = dutchName;
        this.frenchName = frenchName;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result;

        if (obj == this) {
            result = true;
        } else {
            if (obj instanceof AbstractI18nEntity) {
                AbstractI18nEntity entity = (AbstractI18nEntity) obj;

                result = new EqualsBuilder()
                        .append(englishName, entity.englishName)
                        .append(frenchName, entity.frenchName)
                        .append(dutchName, entity.dutchName)
                        .isEquals();
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
                .append(frenchName) //
                .append(dutchName) //
                .toHashCode();
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
    public int compareTo(@SuppressWarnings("NullableProblems") AbstractI18nEntity entity) {
        int result;

        if (entity == null) {
            result = -1;
        } else {
            result = new CompareToBuilder()
                    .append(StringUtils.stripAccents(englishName), StringUtils.stripAccents(entity.getEnglishName()), String.CASE_INSENSITIVE_ORDER)
                    .append(StringUtils.stripAccents(frenchName), StringUtils.stripAccents(entity.getFrenchName()), String.CASE_INSENSITIVE_ORDER)
                    .append(StringUtils.stripAccents(dutchName), StringUtils.stripAccents(entity.getDutchName()), String.CASE_INSENSITIVE_ORDER)
                    .toComparison();
        }

        return result;
    }
}
