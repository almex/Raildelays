package be.raildelays.test;

import be.raildelays.domain.entities.TrainLine;

/**
 * @author Almex
 */
public class RaildelaysTestUtils {

    private static long SEQUENCE = 0;

    public static TrainLine generateTrain(Long routeId) {
        TrainLineTest train = new TrainLineTest(routeId);
        train.setId(SEQUENCE++);

        return train;
    }

    private static class TrainLineTest extends TrainLine {

        public TrainLineTest(Long routeId) {
            super(new Builder(routeId));
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
