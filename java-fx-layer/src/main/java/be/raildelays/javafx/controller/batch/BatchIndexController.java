package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import org.springframework.batch.core.step.job.JobParametersExtractor;

import java.net.URL;
import java.util.ResourceBundle;


public class BatchIndexController implements BatchController, Initializable {

    @FXML
    private VBox mainJob;

    @FXML
    private MainBatchController mainJobController;

    @FXML
    private VBox handleMaxMonthsJob;

    @FXML
    private HandleMaxMonthsBatchController handleMaxMonthsJobController;

    @FXML
    private VBox handleOneHourDelayJob;

    @FXML
    private HandleOneHourDelayBatchController handleOneHourDelayJobController;

    private BatchScheduledService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //noop
    }

    @Override
    public void setService(BatchScheduledService service) {
        this.service = service;
    }

    @Override
    public void destroy() {
        mainJobController.destroy();
        handleOneHourDelayJobController.destroy();
        handleMaxMonthsJobController.destroy();
    }

    @Override
    public void setPropertiesExtractor(JobParametersExtractor propertiesExtractor) {

    }
}
