package be.raildelays.scheduling;

import be.raildelays.delays.TimeDelay;
import be.raildelays.location.Location;

/**
 * @author Almex
 */
public interface Stop {

    Location getLocation();

    TimeDelay getDepartureTime();

    TimeDelay getArrivalTime();
}
