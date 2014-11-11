package be.raildelays.httpclient.impl

import be.raildelays.domain.Language
import be.raildelays.httpclient.AbstractRequest

/**
 * @author Almex
 * @since 2.0
 */
class StationListRequest extends AbstractRequest {

    public StationListRequest(Language language) {
        setLanguage(language)
    }

    public Language getLanguage() {
        return getValue("language");
    }

    private void setLanguage(Language language) {
        setValue(language, "language", Language.class);
    }
}
