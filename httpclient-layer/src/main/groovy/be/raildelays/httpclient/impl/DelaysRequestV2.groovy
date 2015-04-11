package be.raildelays.httpclient.impl

import be.raildelays.domain.Language
import be.raildelays.domain.Sens
import be.raildelays.httpclient.AbstractRequest

/**
 * @author Almex
 * @since 1.2
 */
public class DelaysRequestV2 extends AbstractRequest {

    public DelaysRequestV2(String trainId, Date day, Language language) {
        setTrainId(trainId)
        setDay(day)
        setLanguage(language)
    }

    public String getTrainId() {
        return getValue("trainId");
    }

    private void setTrainId(String trainId) {
        setValue(trainId, "trainId", String.class);
    }

    public Date getDay() {
        return getValue("day");
    }

    private void setDay(Date day) {
        setValue(day, "day", Date.class);
    }

    public Language getLanguage() {
        return getValue("language");
    }

    private void setLanguage(Language language) {
        setValue(language, "language", Language.class);
    }

}
