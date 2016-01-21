package be.raildelays.test;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.TrainLine;

/**
 * @author Almex
 */
public class RaildelaysTestUtils {

    private static long SEQUENCE = 0;

    public static TrainLine generateTrain(String name, Language language) {
        TrainLineTest train = new TrainLineTest(name, language);
        train.setId(SEQUENCE++);

        return train;
    }

    private static class TrainLineTest extends TrainLine {

        public TrainLineTest(String name, Language language) {
            super(name, language);
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
