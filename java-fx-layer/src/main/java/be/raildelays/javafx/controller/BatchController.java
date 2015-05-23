package be.raildelays.javafx.controller;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author Almex
 * @since 1.2
 */
public class BatchController implements Initializable {

    private BatchScheduledService service;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button restartButton;
    @FXML
    private Button abandonButton;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label progressLabel;
    private StackPane progressBarWithLabel;
    private String jobName;

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchController.class);

    private void bindEvents() {
        startButton.setOnAction(event -> doStart());
        stopButton.setOnAction(event -> doStop());
        abandonButton.setOnAction(event -> doAbandon());
        restartButton.setOnAction(event -> doRestart());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize() {
        service.setOnSucceeded((event) -> doRefreshProgress());
        service.setOnFailed((event) -> {
            final Throwable error = service.getException();
            progressLabel.setText("ERROR");
            LOGGER.error("An error occurred!", error);
        });
        service.setDelay(Duration.seconds(1));
        service.setPeriod(Duration.seconds(1));
        service.stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            JobExecution jobExecution = service.getJobExecution();

            if (service.isStarted()) {
                switch (jobExecution.getStatus()) {
                    case STARTING:
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        abandonButton.setDisable(true);
                        restartButton.setDisable(true);
                        break;
                    case STARTED:
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        abandonButton.setDisable(true);
                        restartButton.setDisable(true);
                        break;
                    case COMPLETED:
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        abandonButton.setDisable(true);
                        restartButton.setDisable(true);
                        break;
                    case FAILED:
                        startButton.setDisable(true);
                        stopButton.setDisable(true);
                        abandonButton.setDisable(false);
                        restartButton.setDisable(false);
                        break;
                    case STOPPING:
                        startButton.setDisable(true);
                        stopButton.setDisable(true);
                        abandonButton.setDisable(true);
                        restartButton.setDisable(true);
                        break;
                    case STOPPED:
                        startButton.setDisable(true);
                        stopButton.setDisable(true);
                        abandonButton.setDisable(false);
                        restartButton.setDisable(false);
                        break;
                    case ABANDONED:
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        abandonButton.setDisable(true);
                        restartButton.setDisable(true);
                        break;
                }
            } else {
                startButton.setDisable(false);
                stopButton.setDisable(true);
                abandonButton.setDisable(true);
                restartButton.setDisable(true);
            }
        });
    }

    private void initializeSkin() {
        progressBarWithLabel = new StackPane();
        progressLabel = new Label();
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        restartButton = new Button("Restart");
        abandonButton = new Button("Abandon");
        progressBar = new ProgressBar(0.0);
        progressIndicator = new ProgressIndicator(0.0);
        startButton.setDisable(false);
        stopButton.setDisable(true);
        abandonButton.setDisable(true);
        restartButton.setDisable(true);
        progressBar.setMinWidth(150);
        progressBarWithLabel.getChildren().addAll(progressBar, progressLabel);
    }

    public Pane getPane() {
        final HBox hBox = new HBox();

        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.setFillHeight(false);
        hBox.getChildren().addAll(startButton, stopButton, restartButton, abandonButton, progressBarWithLabel, progressIndicator);

        return hBox;
    }

    public void doRestart() {
        service.restart();
        startButton.setDisable(true);
    }

    public void doAbandon() {
        if (service.abandon()) {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            abandonButton.setDisable(true);
            restartButton.setDisable(true);
            doRefreshProgress();
        }
    }

    public void doStop() {
        if (service.stop()) {
            stopButton.setDisable(true);
        }
    }

    public void doStart() {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        progressBar.setProgress(0.0);
        progressIndicator.setProgress(0.0);
        progressLabel.setText("");

        service.reset();
        service.start(jobName, new Date());
    }

    public void shutdownService() {
        service.stop();
        service.cancel();
        service.reset();
    }

    public void doRefreshProgress() {
        final JobExecution jobExecution = service.getJobExecution();
        final long stepDoneCount = jobExecution.getStepExecutions().stream()
                .filter(stepExecution -> !stepExecution.getStatus().isRunning())
                .count();
        final long stepNumber = jobExecution.getStepExecutions().size();
        double progress = (double) stepDoneCount / stepNumber;
        String status = jobExecution.getStatus().name();

        if (!jobExecution.isRunning()) {
            service.cancel();
            progress = 1;
        }

        progressBar.setProgress(progress);
        progressIndicator.setProgress(progress);
        progressLabel.setText(status);
    }

    public void setService(BatchScheduledService service) {
        this.service = service;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
