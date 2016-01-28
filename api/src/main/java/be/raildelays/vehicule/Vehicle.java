package be.raildelays.vehicule;

import be.raildelays.scheduling.LineDiscriminator;

/**
 * @author Almex
 * @since 2.0
 */
@FunctionalInterface
public interface Vehicle extends LineDiscriminator {
    String getName();
}
