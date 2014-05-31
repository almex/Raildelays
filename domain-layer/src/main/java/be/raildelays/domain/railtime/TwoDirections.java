package be.raildelays.domain.railtime;

/**
 * Combine two directions
 *
 * @author Almex
 */
public class TwoDirections {

    private Direction departure;

    private Direction arrival;

    public TwoDirections(Direction departure, Direction arrival) {
        this.departure = departure;
        this.arrival = arrival;
    }

    public Direction getDeparture() {
        return departure;
    }

    public Direction getArrival() {
        return arrival;
    }

}
