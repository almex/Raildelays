package be.raildelays.javafx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * @author Almex
 * @since 1.2
 */
public class DataPreLoader extends Preloader {

    private ProgressBar progressBar;
    private StackPane root;
    private StackPane background;
    private Scene preLoaderScene;
    private Stage preLoaderStage;
    private long startDownload = -1;
    private Timeline simulatorTimeline;

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof PreLoaderHandoverEvent) {
            // handover from preloader to application
            final PreLoaderHandoverEvent event = (PreLoaderHandoverEvent) info;

            Platform.runLater(event.getDataLoadingTask());
        } else if (info instanceof StateChangeNotification &&
                ((StateChangeNotification) info).getType() == StateChangeNotification.Type.BEFORE_START) {
            //hide after get any state update from application
            preLoaderStage.hide();
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_INIT) {
            // check for fast download and restart progress
            if ((System.currentTimeMillis() - startDownload) < 500) {
                progressBar.setProgress(0);
            }
            // we have finished downloading application, now we are
            // running application init() method, as we have no way
            // of calculating real progress
            // simplate pretend progress here
            simulatorTimeline = new Timeline();
            simulatorTimeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(15),
                            new KeyValue(progressBar.progressProperty(), 1)));
            simulatorTimeline.play();
        }
    }

    @Override
    public void handleProgressNotification(ProgressNotification info) {
        if (startDownload == -1) {
            startDownload = System.currentTimeMillis();
        }
        progressBar.setProgress(info.getProgress() * 0.5);
    }

    @Override
    public void init() throws Exception {
        root = new StackPane();
        progressBar = new ProgressBar(0.0);
        background = new StackPane();
        background.setId("Window");
        background.setCache(true);
        root.getChildren().addAll(background, progressBar);
        Platform.runLater(() -> preLoaderScene = new Scene(root, 300, 200));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.preLoaderStage = primaryStage;
        preLoaderScene.setFill(Color.TRANSPARENT);
        preLoaderStage.initStyle(StageStyle.TRANSPARENT);
        preLoaderStage.setScene(preLoaderScene);
        preLoaderStage.show();
    }

}
