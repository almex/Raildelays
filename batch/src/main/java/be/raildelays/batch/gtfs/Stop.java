package be.raildelays.batch.gtfs;

import java.net.URL;
import java.util.TimeZone;

/**
 * Bean representation of GTFS stops.txt file.
 *
 * @author Almex
 * @since 2.0
 */
public class Stop {

    private String stopId;
    private String stopCode;
    private String stopName;
    private String stopDesc;
    private String stopLat;
    private String stopLon;
    private String zoneId;
    private URL stopUrl;
    private LocationType locationType;
    private String parentStation;
    private TimeZone stopTimezone;
    private Accessibility wheelchairBoarding;


    public enum LocationType {
        NOT_PHYSICAL, PHYSICAL;

        public static LocationType valueOfIndex(int index) {
            return index >= values().length || index < 0 ? null : values()[index];
        }
    }

    public enum Accessibility {
        NOT_ALLOWED, ALLOWED
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getStopCode() {
        return stopCode;
    }

    public void setStopCode(String stopCode) {
        this.stopCode = stopCode;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopDesc() {
        return stopDesc;
    }

    public void setStopDesc(String stopDesc) {
        this.stopDesc = stopDesc;
    }

    public String getStopLat() {
        return stopLat;
    }

    public void setStopLat(String stopLat) {
        this.stopLat = stopLat;
    }

    public String getStopLon() {
        return stopLon;
    }

    public void setStopLon(String stopLon) {
        this.stopLon = stopLon;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public URL getStopUrl() {
        return stopUrl;
    }

    public void setStopUrl(URL stopUrl) {
        this.stopUrl = stopUrl;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public String getParentStation() {
        return parentStation;
    }

    public void setParentStation(String parentStation) {
        this.parentStation = parentStation;
    }

    public TimeZone getStopTimezone() {
        return stopTimezone;
    }

    public void setStopTimezone(TimeZone stopTimezone) {
        this.stopTimezone = stopTimezone;
    }

    public Accessibility getWheelchairBoarding() {
        return wheelchairBoarding;
    }

    public void setWheelchairBoarding(Accessibility wheelchairBoarding) {
        this.wheelchairBoarding = wheelchairBoarding;
    }
}
