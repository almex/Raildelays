package be.raildelays.scheduling;

import be.raildelays.delays.TimestampDelay;
import be.raildelays.location.Location;

/**
 * @author Almex
 */
public interface Stop {

    Location getLocation();

    TimestampDelay getDepartureTime();

    TimestampDelay getArrivalTime();
}
