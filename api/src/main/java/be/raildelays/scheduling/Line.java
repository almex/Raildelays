package be.raildelays.scheduling;

import java.util.List;

/**
 * @param <D> is the discriminator to which this line belongs. It could be a {@code Vehicle}.
 * @param <T> an extend of {@link Stop}
 * @author Almex
 * @since 2.0
 */
public interface Line<D extends LineDiscriminator, T extends Stop> {
    D getDiscriminator();

    List<T> getStops();
}
