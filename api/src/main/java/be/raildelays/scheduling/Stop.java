package be.raildelays.scheduling;

import be.raildelays.delays.TimeDelay;
import be.raildelays.location.Location;

/**
 * @author Almex
 * @since 2.0
 */
public interface Stop<L extends Location, I extends Line> {

    I getLine();

    L getLocation();

    TimeDelay getDepartureTime();

    TimeDelay getArrivalTime();
}
