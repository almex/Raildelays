package be.raildelays.scheduling;

import java.util.List;

/**
 * @author Almex
 * @since 2.0
 * @param <D> is the discriminator to which this line belongs. It could be a {@code Vehicle}.
 */
public interface Line<D extends LineDiscriminator> {
    D getDiscriminator();

    List<Stop> getStops();
}
