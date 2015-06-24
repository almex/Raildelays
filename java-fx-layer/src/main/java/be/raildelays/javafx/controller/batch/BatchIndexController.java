package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;


public class BatchIndexController implements BatchController, Initializable {

    @FXML
    private VBox mainJob;

    @FXML
    private BatchController mainJobController;

    @FXML
    private VBox handleMaxMonthsJob;

    @FXML
    private BatchController handleMaxMonthsJobController;

    @FXML
    private VBox handleOneHourDelayJob;

    @FXML
    private BatchController handleOneHourDelayJobController;

    private BatchScheduledService service;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainJobController.setService(service);
        handleMaxMonthsJobController.setService(service);
        handleOneHourDelayJobController.setService(service);
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
}
