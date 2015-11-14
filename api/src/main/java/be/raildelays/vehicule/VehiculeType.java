package be.raildelays.vehicule;

/**
 * The route_type field describes the type of transportation used on a route. Valid values for this field are:
 * <p>
 * 0 - Tram, Streetcar, Light rail. Any light rail or street level system within a metropolitan area.
 * 1 - Subway, Metro. Any underground rail system within a metropolitan area.
 * 2 - Rail. Used for intercity or long-distance travel.
 * 3 - Bus. Used for short- and long-distance bus routes.
 * 4 - Ferry. Used for short- and long-distance boat service.
 * 5 - Cable car. Used for street-level cable cars where the cable runs beneath the car.
 * 6 - Gondola, Suspended cable car. Typically used for aerial cable cars where the car is suspended from the cable.
 * 7 - Funicular. Any rail system designed for steep inclines.
 * <p>
 * See a Google Maps screenshot highlighting the route_type.
 *
 * @author Almex
 * @since 2.0
 */
public enum VehiculeType {
    TRAIN(Train.class), BUS(Bus.class), FERRY(Ferry.class), SUBWAY(Subway.class), TAXI(Taxi.class), TRAMWAY(Tramway.class);

    private Class<? extends Vehicle> type;

    VehiculeType(Class<? extends Vehicle> type) {
        this.type = type;
    }
}
