package be.raildelays.httpclient.impl

import be.raildelays.domain.Language
import be.raildelays.domain.Sens
import be.raildelays.httpclient.AbstractRequest

/**
 * @author Almex
 * @since 2.0
 */
public class DelaysRequest extends AbstractRequest {

    public DelaysRequest(String trainId, Date day, Sens sens, Language language) {
        setTrainId(trainId)
        setDay(day)
        setSens(sens)
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

    public Sens getSens() {
        return getValue("sens");
    }

    private void setSens(Sens sens) {
        setValue(sens, "sens", String.class);
    }

    public Language getLanguage() {
        return getValue("language");
    }

    private void setLanguage(Language language) {
        setValue(language, "language", Language.class);
    }

}
