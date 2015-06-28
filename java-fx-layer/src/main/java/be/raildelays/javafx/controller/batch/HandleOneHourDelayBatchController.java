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

            builder.addString("more.than.one.hour.excel.path", file.getAbsolutePath());

            service.reset();
            service.start(jobName, builder.toJobParameters());

            doRefreshProgress();
        }
    }

    public void onBrowse() {
        FileChooser fileChooser = new FileChooser();
        File directory = new File(propertiesExtractor.getJobParameters(null, null).getString("excel.output.path"));
        fileChooser.setInitialDirectory(directory);

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
