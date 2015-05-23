package be.raildelays.javafx;

import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.scene.Parent;

/**
 * @author Almex
 * @since 1.2
 */
public class PreLoaderHandoverEvent implements Preloader.PreloaderNotification {
    private final Parent root;
    private final String cssUrl;
    private final Task<Void> dataLoadingTask;

    public PreLoaderHandoverEvent(Parent root, String cssUrl, Task<Void> dataLoadingTask) {
        this.root = root;
        this.cssUrl = cssUrl;
        this.dataLoadingTask = dataLoadingTask;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    public Parent getRoot() {
        return root;
    }

    public Task<Void> getDataLoadingTask() {
        return dataLoadingTask;
    }
}
