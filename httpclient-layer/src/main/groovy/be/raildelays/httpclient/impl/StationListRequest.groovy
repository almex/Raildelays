package be.raildelays.httpclient.impl

import be.raildelays.httpclient.AbstractRequest

/**
 * @author Almex
 * @since 2.0
 */
class StationListRequest extends AbstractRequest {

    public StationListRequest(String language) {
        setLanguage(language)
    }

    public String getLanguage() {
        return getValue("language");
    }

    public void setLanguage(String language) {
        setValue(language, "language", String.class);
    }
}
