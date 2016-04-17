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
 * @since 2.0
 */
public class DownloadListOfTrainsBatchController extends AbstractBatchController {
    @FXML
    private DatePicker date;

    @Override
    public void doStart() {
        if (date != null) {
            super.doStart();
        }
    }

    @Override
    protected void addExtraJobParameters(JobParametersBuilder builder) {
        builder.addDate("date", Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setJobName("loadGtfsIntoDatabaseJob");
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
}
