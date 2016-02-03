package be.raildelays.batch.gtfs;

import java.util.Collections;
import java.util.List;

/**
 * Bean representation of GTFS trips.txt file.
 *
 * @author Almex
 * @since 2.0
 */
public class Trip {

    private String routeId;
    private String serviceId;
    private String tripId;
    private String tripHeadsign;
    private String tripShortName;
    private String directionId;
    private String blockId;
    private String shapeId;
    private Accessibility wheelchairAccessible;
    private Accessibility bikesAllowed;
    private List<StopTime> stopTimes;

    /**
     * Default constructor.
     */
    public Trip() {
        stopTimes = Collections.emptyList();
    }

    public enum Accessibility {
        ALLOWED, NOT_ALLOWED;

        public static Accessibility valueForIndex(int index) {
            Accessibility result = null;

            if (index == 1) {
                result = ALLOWED;
            } else if (index == 2) {
                result = NOT_ALLOWED;
            }

            return result;
        }
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public void setTripShortName(String tripShortName) {
        this.tripShortName = tripShortName;
    }

    public String getDirectionId() {
        return directionId;
    }

    public void setDirectionId(String directionId) {
        this.directionId = directionId;
    }

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getShapeId() {
        return shapeId;
    }

    public void setShapeId(String shapeId) {
        this.shapeId = shapeId;
    }

    public Accessibility getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(Accessibility wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public Accessibility getBikesAllowed() {
        return bikesAllowed;
    }

    public void setBikesAllowed(Accessibility bikesAllowed) {
        this.bikesAllowed = bikesAllowed;
    }

    public List<StopTime> getStopTimes() {
        return stopTimes;
    }

    public void setStopTimes(List<StopTime> stopTimes) {
        this.stopTimes = stopTimes;
    }
}

