/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Almex
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package be.raildelays.javafx.controller.batch;

import be.raildelays.javafx.service.BatchScheduledService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.step.job.JobParametersExtractor;

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
    protected String jobName;
    protected BatchScheduledService service;
    protected JobParametersExtractor propertiesExtractor;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service.setOnSucceeded((event) -> doRefreshProgress());
        service.setOnFailed((event) -> {
            final Throwable error = service.getException();
            progressLabel.setText("ERROR");
            LOGGER.error("An error occurred!", error);
        });
        service.setDelay(Duration.seconds(1));
        service.setPeriod(Duration.seconds(1));
        service.stateProperty().addListener(getStateChangeListener());
        progressLabel.setText("");

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

    public abstract void doStart();

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

    public void setPropertiesExtractor(JobParametersExtractor propertiesExtractor) {
        this.propertiesExtractor = propertiesExtractor;
    }
}
