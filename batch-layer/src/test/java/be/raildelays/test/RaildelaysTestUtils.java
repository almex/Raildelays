package be.raildelays.test;

import be.raildelays.domain.Language;
import be.raildelays.domain.entities.Train;

/**
 * @author Almex
 */
public class RaildelaysTestUtils {

    private static long SEQUENCE = 0;

    private static class TrainTest extends Train {

        public TrainTest(String name, Language language) {
            super(name, language);
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public static Train generateTrain(String name, Language language) {
        TrainTest train = new TrainTest(name, language);
        train.setId(SEQUENCE++);

        return train;
    }
}
