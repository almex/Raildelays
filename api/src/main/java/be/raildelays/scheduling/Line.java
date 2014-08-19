package be.raildelays.scheduling;

import java.util.List;

/**
 * @param <D> is the discriminator to which this line belong it could be a vehicle
 */
public interface Line<D extends LineDiscriminator> {
    D getDiscriminator();

    List<Stop> getStops();
}
