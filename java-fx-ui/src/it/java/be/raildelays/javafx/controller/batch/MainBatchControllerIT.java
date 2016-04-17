package be.raildelays.javafx.controller.batch;

import javafx.fxml.FXMLLoader;
import org.junit.Before;

/**
 * @author Almex
 */
public class MainBatchControllerIT extends AbstractBatchControllerIT<MainBatchController> {


    @Before
    public void setUp() throws Exception {
        rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/main-job.fxml"));
        controller = new MainBatchController();

        super.setUp();
    }

}