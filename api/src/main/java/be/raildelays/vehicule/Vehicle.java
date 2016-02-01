package be.raildelays.vehicule;

import be.raildelays.scheduling.LineDiscriminator;

/**
 * @author Almex
 * @since 2.0
 */
public interface Vehicle extends LineDiscriminator {
    String getName();
}
