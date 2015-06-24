package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Almex
 * @since 1.2
 */
public abstract class AbstractBatchController implements Initializable, BatchController {

    @FXML
    protected Button startButton;
    @FXML
    protected Button stopButton;
    @FXML
    protected Button restartButton;
    @FXML
    protected Button abandonButton;
    @FXML
    protected ProgressBar progressBar;
    @FXML
    protected ProgressIndicator progressIndicator;
    @FXML
    protected Label progressLabel;
    protected StackPane progressBarWithLabel;
    protected String jobName;
    protected BatchScheduledService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchController.class);

    private void bindEvents() {
        startButton.setOnAction(event -> doStart());
        stopButton.setOnAction(event -> doStop());
        abandonButton.setOnAction(event -> doAbandon());
        restartButton.setOnAction(event -> doRestart());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.service = service;

        this.service.setOnSucceeded((event) -> doRefreshProgress());
        this.service.setOnFailed((event) -> {
            final Throwable error = service.getException();
            progressLabel.setText("ERROR");
            LOGGER.error("An error occurred!", error);
        });
        this.service.setDelay(Duration.seconds(1));
        this.service.setPeriod(Duration.seconds(1));
        this.service.stateProperty().addListener(getStateChangeListener());

        resetButtons();
    }

    protected ChangeListener<Worker.State> getStateChangeListener() {
        return (ObservableValue<? extends Worker.State> observable,
                                             Worker.State oldValue,
                                             Worker.State newValue) -> {
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
                        resetButtons();
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
                        resetButtons();
                        break;
                }
            } else {
                resetButtons();
            }
        };
    }

    protected void resetButtons() {
        startButton.setDisable(false);
        stopButton.setDisable(true);
        abandonButton.setDisable(true);
        restartButton.setDisable(true);
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
        stopButton.setDisable(false);
        abandonButton.setDisable(true);
        restartButton.setDisable(true);

        doRefreshProgress();
    }

    public void doAbandon() {
        if (service.abandon()) {
            resetButtons();

            doRefreshProgress();
        }
    }

    public void doStop() {
        if (service.stop()) {
            stopButton.setDisable(true);
            stopButton.setDisable(true);
            abandonButton.setDisable(true);
            restartButton.setDisable(true);

            doRefreshProgress();
        }
    }

    abstract void doStart();

    @Override
    public void destroy() {
        service.stop();
        service.cancel();
        service.reset();
    }

    public void doRefreshProgress() {
        if (service.isStarted()) {
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
    }

    public void setService(BatchScheduledService service) {
        this.service = service;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
}
