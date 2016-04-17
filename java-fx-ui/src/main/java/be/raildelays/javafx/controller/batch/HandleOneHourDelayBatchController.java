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
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Almex
 * @since 1.2
 */
public class HandleOneHourDelayBatchController extends AbstractBatchController {
    @FXML
    private Button browse;

    private File file;

    @Override
    public void doStart() {
        if (file != null) {
            super.doStart();
        }
    }

    @Override
    protected void addExtraJobParameters(JobParametersBuilder builder) {
        builder.addString("more.than.one.hour.excel.path", file.getAbsolutePath());
    }

    public void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        JobParameters jobParameters = propertiesExtractor.getJobParameters(null, null);
        File directory = new File(jobParameters.getString("excel.output.path"));

        fileChooser.setInitialDirectory(directory);
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Excel files", "*.xls", "*.xlsx");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setSelectedExtensionFilter(extensionFilter);
        fileChooser.setTitle("Choose the Excel file to parse");

        //Show open file dialog
        file = fileChooser.showOpenDialog(null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setJobName("handleMoreThanOneHourDelaysJob");
        super.initialize(location, resources);
    }

    public void setBrowse(Button browse) {
        this.browse = browse;
    }
}
