package be.raildelays.scheduling;

import be.raildelays.delays.TimeDelay;
import be.raildelays.location.Location;

/**
 * @author Almex
 * @since 2.0
 */
public interface Stop<T extends Location> {

    T getLocation();

    TimeDelay getDepartureTime();

    TimeDelay getArrivalTime();
}
