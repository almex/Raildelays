/**
 *
 */
package be.raildelays.domain;

/**
 * Enumerate all languages handled by the different SNCB/NMBS websites
 * This list also different parameters value that should be used for
 * b-rail.be, railtime.be or railtime.be/mobile.
 *
 * @author Almex
 */
public enum Language {

    EN("en", "EN", "en", "eny"),
    FR("fr", "FR", "fr", "fny"),
    NL("nl", "NL", "nl", "nny");

    private String railtimeParameter;
    private String mobileParameter;
    private String bRailParameter;
    private String sncbParameter;

    Language(String railtimeParameter, String mobileParameter, String bRailParameter, String sncbParameter) {
        this.bRailParameter = bRailParameter;
        this.mobileParameter = mobileParameter;
        this.railtimeParameter = railtimeParameter;
        this.sncbParameter = sncbParameter;
    }

    public String getRailtimeParameter() {
        return railtimeParameter;
    }

    public String getMobileParameter() {
        return mobileParameter;
    }

    public String getbRailParameter() {
        return bRailParameter;
    }

    public String getSncbParameter() {
        return sncbParameter;
    }
}
