package be.raildelays.batch.gtfs;

import java.time.LocalTime;

/**
 * Bean representation of GTFS stop_times.txt file.
 *
 * @author Almex
 * @since 2.0
 */
public class StopTime {

    private String tripId;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String stopId;
    private Integer stopSequence;
    private String stopHeadsign;
    private Type pickupType;
    private Type dropOffType;
    private Double shapeDistTraveled;
    private Timepoint timepoint;


    public enum Type {
        REGULARLY, NONE, MUST_PHONE, MUST_COORDINATE
    }

    public enum Timepoint {
        APPROXIMATE, EXACT
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public String getStopHeadsign() {
        return stopHeadsign;
    }

    public void setStopHeadsign(String stopHeadsign) {
        this.stopHeadsign = stopHeadsign;
    }

    public Type getPickupType() {
        return pickupType;
    }

    public void setPickupType(Type pickupType) {
        this.pickupType = pickupType;
    }

    public Type getDropOffType() {
        return dropOffType;
    }

    public void setDropOffType(Type dropOffType) {
        this.dropOffType = dropOffType;
    }

    public Double getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    public void setShapeDistTraveled(Double shapeDistTraveled) {
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public Timepoint getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(Timepoint timepoint) {
        this.timepoint = timepoint;
    }
}
