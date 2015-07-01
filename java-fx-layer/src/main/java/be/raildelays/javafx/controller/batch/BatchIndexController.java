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
