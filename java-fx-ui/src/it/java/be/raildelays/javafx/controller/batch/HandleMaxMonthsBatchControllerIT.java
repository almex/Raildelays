package be.raildelays.javafx.controller.batch;

import javafx.fxml.FXMLLoader;
import org.junit.Before;

/**
 * @author Almex
 */
public class HandleMaxMonthsBatchControllerIT extends AbstractBatchControllerIT<HandleMaxMonthsBatchController> {

    @Before
    public void setUp() throws Exception {
        rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/handle-max-months-job.fxml"));
        controller = new HandleMaxMonthsBatchController();

        super.setUp();
    }

}