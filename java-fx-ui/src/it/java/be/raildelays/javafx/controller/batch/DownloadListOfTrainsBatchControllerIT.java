package be.raildelays.javafx.controller.batch;

import javafx.fxml.FXMLLoader;
import org.junit.Before;

/**
 * @author Almex
 */
public class DownloadListOfTrainsBatchControllerIT extends AbstractBatchControllerIT<DownloadListOfTrainsBatchController> {


    @Before
    public void setUp() throws Exception {
        rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/load-gtfs-into-database-job.fxml"));
        controller = new DownloadListOfTrainsBatchController();

        super.setUp();
    }

}