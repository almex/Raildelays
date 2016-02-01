package be.raildelays.batch.gtfs;

import java.net.URL;

/**
 * Bean representation of GTFS routes.txt file.
 *
 * @author Almex
 * @since 2.0
 */
public class Route {

    private String routeId;
    private String agencyId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private RouteType routeType;
    private URL routeUrl;
    private String routeColor;
    private String routeTextColor;

    public enum RouteType {
        TRAM, SUBWAY, RAIL, BUS, FERRY, CABLE_CAR, GONDOLA, FUNICULAR
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public void setRouteShortName(String routeShortName) {
        this.routeShortName = routeShortName;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public void setRouteDesc(String routeDesc) {
        this.routeDesc = routeDesc;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public URL getRouteUrl() {
        return routeUrl;
    }

    public void setRouteUrl(URL routeUrl) {
        this.routeUrl = routeUrl;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public void setRouteColor(String routeColor) {
        this.routeColor = routeColor;
    }

    public String getRouteTextColor() {
        return routeTextColor;
    }

    public void setRouteTextColor(String routeTextColor) {
        this.routeTextColor = routeTextColor;
    }
}
