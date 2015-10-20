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

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Almex
 * @since 2.0
 */
public class DownloadListOfTrainsBatchController extends AbstractBatchController {

    @Override
    public void doStart() {
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

        service.reset();
        service.start(jobName, builder.toJobParameters());

        doRefreshProgress();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setJobName("loadGtfsIntoDatabaseJob");
        super.initialize(location, resources);
    }
}
