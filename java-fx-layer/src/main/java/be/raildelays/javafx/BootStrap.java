package be.raildelays.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Almex
 */
public class BootStrap extends Application {

    private MenuItem calculateItem;
    private Button startButton;
    private Button stopButton;
    private Button restartButton;
    private Button abandonButton;
    private Scene scene;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    private Label informationLabel;
    private BatchScheduledService batchScheduledService;

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);


    @Override
    public void start(Stage primaryStage) {
        final Menu exitItem = new Menu("Exit");
        final Menu actionMenu = new Menu("Action");
        final MenuBar menuBar = new MenuBar();
        final StackPane progressBarWithLabel = new StackPane();
        final BorderPane root = new BorderPane();
        final HBox hBox = new HBox();
        final String[] contextPaths = new String[]{"/spring/bootstrap-fx-context.xml"};

        batchScheduledService = new BatchScheduledService(new ClassPathXmlApplicationContext(contextPaths));
        batchScheduledService.setDelay(Duration.seconds(1));
        batchScheduledService.setPeriod(Duration.seconds(1));
        batchScheduledService.stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            JobExecution jobExecution = batchScheduledService.getJobExecution();

            if (batchScheduledService.isStarted()) {
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


        informationLabel = new Label();
        calculateItem = new MenuItem("Compute");
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        restartButton = new Button("Restart");
        abandonButton = new Button("Abandon");
        progressBar = new ProgressBar(0.0);
        progressIndicator = new ProgressIndicator(0.0);
        scene = new Scene(root, 640, 480);

        exitItem.setOnAction(event -> Platform.exit());
        calculateItem.setOnAction(event -> doStart());
        startButton.setOnAction(event -> doStart());
        stopButton.setOnAction(event -> doStop());
        abandonButton.setOnAction(event -> doAbandon());
        restartButton.setOnAction(event -> doRestart());
        startButton.setDisable(false);
        stopButton.setDisable(true);
        abandonButton.setDisable(true);
        restartButton.setDisable(true);
        progressBar.setMinWidth(150);

        actionMenu.getItems().add(calculateItem);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.setFillHeight(false);
        progressBarWithLabel.getChildren().addAll(progressBar, informationLabel);
        hBox.getChildren().addAll(startButton, stopButton, restartButton, abandonButton, progressBarWithLabel, progressIndicator);
        menuBar.getMenus().setAll(exitItem, actionMenu);

        root.setTop(menuBar);
        root.setCenter(hBox);
        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void doRestart() {
        batchScheduledService.restart();
        startButton.setDisable(true);
    }

    private void doAbandon() {
        if (batchScheduledService.abandon()) {
            startButton.setDisable(false);
            stopButton.setDisable(true);
            abandonButton.setDisable(true);
            restartButton.setDisable(true);
            refreshProgress(Cursor.DEFAULT);
        }
    }

    private void doStop() {
        if (batchScheduledService.stop()) {
            stopButton.setDisable(true);
        }
    }

    private void doStart() {
        final Cursor oldCursor = scene.getCursor();

        scene.setCursor(Cursor.WAIT);
        startButton.setDisable(true);
        stopButton.setDisable(false);
        progressBar.setProgress(0.0);
        progressIndicator.setProgress(0.0);
        informationLabel.setText("");

        batchScheduledService.setOnSucceeded((event) -> refreshProgress(oldCursor));
        batchScheduledService.setOnFailed((event) -> {
            final Throwable error = batchScheduledService.getException();
            informationLabel.setText("ERROR");
            LOGGER.error("An error occurred!", error);
        });

        batchScheduledService.reset();
        batchScheduledService.start();
    }

    private void refreshProgress(Cursor oldCursor) {
        final JobExecution jobExecution = batchScheduledService.getJobExecution();
        final long stepDoneCount = jobExecution.getStepExecutions().stream()
                .filter(stepExecution -> !stepExecution.getStatus().isRunning())
                .count();
        final long stepNumber = jobExecution.getStepExecutions().size();
        double progress = (double) stepDoneCount / stepNumber;
        String status = jobExecution.getStatus().name();

        if (!jobExecution.isRunning()) {
            scene.setCursor(oldCursor);
            batchScheduledService.cancel();
            progress = 1;
        }

        progressBar.setProgress(progress);
        progressIndicator.setProgress(progress);
        informationLabel.setText(status);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
