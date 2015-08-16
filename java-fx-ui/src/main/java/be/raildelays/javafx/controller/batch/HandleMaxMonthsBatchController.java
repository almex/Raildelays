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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * @author Almex
 * @since 1.2
 */
public class HandleMaxMonthsBatchController extends AbstractBatchController {
    @FXML
    private DatePicker date;

    @Override
    public void doStart() {
        if (date.getValue() != null) {
            JobParameters jobParameters = propertiesExtractor.getJobParameters(null, null);
            JobParametersBuilder builder = new JobParametersBuilder(jobParameters);

            startButton.setDisable(true);
            stopButton.setDisable(false);
            abandonButton.setDisable(true);
            restartButton.setDisable(true);
            progressBar.setProgress(0.0);
            progressIndicator.setProgress(0.0);
            progressLabel.setText("");

            if (service.isRunning()) {
                service.cancel();
            }


            builder.addDate("threshold.date", Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

            service.reset();
            service.start(jobName, builder.toJobParameters());

            doRefreshProgress();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        setJobName("handleMaxMonthsJob");
        date.setValue(LocalDate.now());
        date.setDayCellFactory(param -> new DateCell() {

            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now().minusDays(6)) ||
                        item.isAfter(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });
        super.initialize(location, resources);
    }

    @Override
    protected ChangeListener<Worker.State> getStateChangeListener() {

        return (ObservableValue<? extends Worker.State> observable,
                Worker.State oldValue,
                Worker.State newValue) -> {
            super.getStateChangeListener().changed(observable, oldValue, newValue);
            date.setDisable(startButton.isDisable());
        };

    }

    @Override
    protected void resetButtons() {
        super.resetButtons();
        date.setDisable(startButton.isDisable());
    }
}
