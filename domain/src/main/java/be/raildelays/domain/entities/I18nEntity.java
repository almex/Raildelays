/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

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
public class I18nEntity extends AbstractEntity implements Serializable, Comparable<I18nEntity> {

    @Column(name = "ENGLISH_NAME")
    protected String englishName;
    @Column(name = "FRENCH_NAME")
    protected String frenchName;
    @Column(name = "DUTCH_NAME")
    protected String dutchName;

    /**
     * Default contrcutor.
     */
    protected I18nEntity() {
        this.englishName = "";
        this.dutchName = "";
        this.frenchName = "";
    }

    /**
     * Initialization constructor.
     *
     * @param englishName English name for this trainLine station.
     */
    public I18nEntity(final String englishName) {
        this(englishName, "", "");
    }

    /**
     * Initialization constructor.
     *
     * @param name for this trainLine station.
     */
    public I18nEntity(final String name, Language language) {
        if (language != null) {
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
    }

    /**
     * Initialization constructor.
     *
     * @param englishName English name for this trainLine
     * @param dutchName   Dutch name for this trainLine
     * @param frenchName  French name for this trainLine
     */
    public I18nEntity(final String englishName, final String dutchName, final String frenchName) {
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
            if (obj instanceof I18nEntity) {
                I18nEntity entity = (I18nEntity) obj;

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

    public String getName(Language language) {
        String result = null;

        if (language != null) {
            switch (language) {
                case EN:
                    result = getEnglishName();
                    break;
                case NL:
                    result = getDutchName();
                    break;
                case FR:
                    result = getFrenchName();
                    break;
                default:
                    result = "";
            }
        }

        return result;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") I18nEntity entity) {
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

    public static String getNotNullName(I18nEntity entity) {
        String result = "";

        if (entity != null) {
            if (StringUtils.isNotBlank(entity.getEnglishName())) {
                result = entity.getEnglishName();
            } else if (StringUtils.isNotBlank(entity.getFrenchName())) {
                result = entity.getFrenchName();
            } else if (StringUtils.isNotBlank(entity.getDutchName())) {
                result = entity.getDutchName();
            }
        }

        return result;
    }
}
