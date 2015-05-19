package be.raildelays.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * @author Almex
 */
public class BootStrap extends Application {

    private MenuItem calculateItem;
    private Button calculateButton;
    private Scene scene;
    public static final int MAX_ITERATIONS = 1000000;
    private ProgressBar progressBar;
    private ProgressIndicator progressIndicator;
    private Label informationLabel;


    @Override
    public void start(Stage primaryStage) {
        final Menu exitItem = new Menu("Exit");
        final Menu actionMenu = new Menu("Action");
        final MenuBar menuBar = new MenuBar();
        final StackPane center = new StackPane();
        final BorderPane root = new BorderPane();
        final HBox hBox = new HBox();

        informationLabel = new Label("Progress 0%");
        calculateItem = new MenuItem("Compute");
        calculateButton = new Button("Start !");
        progressBar = new ProgressBar(0.0);
        progressIndicator = new ProgressIndicator(0.0);
        scene = new Scene(root, 300, 250);

        exitItem.setOnAction(event -> Platform.exit());
        calculateItem.setOnAction(event -> doCalculate());
        calculateButton.setOnAction(event -> doCalculate());

        actionMenu.getItems().add(calculateItem);
        hBox.setSpacing(5);
        hBox.setAlignment(Pos.CENTER);
        hBox.setFillHeight(false);
        hBox.getChildren().addAll(informationLabel, progressBar, progressIndicator);
        menuBar.getMenus().setAll(exitItem, actionMenu);

        //center.getChildren().add(calculateButton);
        center.getChildren().add(hBox);
        root.setTop(menuBar);
        root.setCenter(center);
        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void doCalculate() {
        final Cursor oldCursor = scene.getCursor();
        final Service<Double > computationService = new Service<Double >(){

            @Override
            protected Task<Double> createTask() {
                return new Task<Double >(){

                    @Override
                    protected Double  call() throws Exception {
                        double result = 0;

                        for (int i = 0; i < MAX_ITERATIONS; i++) {
                            final int iteration = i;

                            if (isCancelled()) {
                                break;
                            }

                            result = (double) i / MAX_ITERATIONS;

                            Platform.runLater(() -> {
                                updateProgress(iteration , MAX_ITERATIONS);
                                updateMessage(String.format("Progress %d%%", (int) (iteration / MAX_ITERATIONS * 100)));
                            });

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ie) {
                                if (isCancelled()) {
                                    break;
                                }
                            }

                        }

                        return result;
                    }

                };
            }
        };

        progressBar.progressProperty().bind(computationService.progressProperty());
        progressIndicator.progressProperty().bind(computationService.progressProperty());
        informationLabel.textProperty().bind(computationService.messageProperty());
        scene.setCursor(Cursor.WAIT);
        calculateItem.setDisable(true);
        calculateButton.setDisable(true);

        /**
         * Service scheduler
         */
        computationService.stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case READY:
                    break;
                case SCHEDULED:
                    break;
                case RUNNING:
                    //progressIndicator.setProgress(computationService.getProgress());
                    //progressBar.setProgress(computationService.getProgress());
                    break;
                case FAILED:
                    break;
                case CANCELLED:
                    break;
                case SUCCEEDED:
                    scene.setCursor(oldCursor);
                    break;
            }
        });

        computationService.setOnSucceeded((WorkerStateEvent event) -> {
            scene.setCursor(oldCursor);
        });
        computationService.setOnFailed((WorkerStateEvent event) -> {
            final Throwable error = computationService.getException();
        });

        computationService.start();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
