package be.raildelays.location;

/**
 * @author Almex
 * @since 2.0
 */
public interface Route<T extends Location> {

    T getDeparture();

    T getDestination();

}
