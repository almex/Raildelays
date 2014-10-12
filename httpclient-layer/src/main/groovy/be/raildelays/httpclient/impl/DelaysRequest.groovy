package be.raildelays.httpclient.impl;

import be.raildelays.httpclient.AbstractRequest;

import java.util.Date;

/**
 * @author Almex
 * @since 2.0
 */
public class DelaysRequest extends AbstractRequest {

    public DelaysRequest(String trainId, Date day, String sens, String language) {
        setTrainId(trainId)
        setDay(day)
        setSens(sens)
        setLanguage(language)
    }

    public String getTrainId() {
        return getValue("trainId");
    }

    public void setTrainId(String trainId) {
        setValue(trainId, "trainId", String.class);
    }

    public Date getDay() {
        return getValue("day");
    }

    public void setDay(Date day) {
        setValue(day, "day", Date.class);
    }

    public String getSens() {
        return getValue("sens");
    }

    public void setSens(String sens) {
        setValue(sens, "sens", String.class);
    }

    public String getLanguage() {
        return getValue("language");
    }

    public void setLanguage(String language) {
        setValue(language, "language", String.class);
    }

}
