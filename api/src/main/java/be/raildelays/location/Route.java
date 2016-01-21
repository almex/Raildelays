package be.raildelays.location;

import be.raildelays.scheduling.Stop;

/**
 * @author Almex
 * @since 2.0
 */
public interface Route<T extends Stop> {

    T getDeparture();

    T getDestination();

}
