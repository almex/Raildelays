package be.raildelays.javafx;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.controller.batch.*;
import be.raildelays.javafx.service.BatchScheduledService;
import be.raildelays.javafx.spring.CountBeanPostProcessor;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Bootstrap for JavaFX UI.
 *
 * @author Almex
 * @since 1.2
 */
public class Bootstrap extends Application {

    private BatchController controller;
    private TabPane root;
    private Stage stage;
    private Scene scene;
    private ClassPathXmlApplicationContext applicationContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    @Override
    public void start(Stage primaryStage) throws IOException {
        notifyPreloader(new PreLoaderHandoverEvent(root,
                null,
                new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        doStart(primaryStage);

                        return null;
                    }
                }));
    }

    @Override
    public void init() throws Exception {
        final String[] contextPaths = new String[]{
                "/spring/bootstrap-fx-context.xml",
                "/jobs/main-job-context.xml",
                "/jobs/steps/handle-max-months-job-context.xml",
                "/jobs/steps/handle-more-than-one-hour-delays-job-context.xml"
        };

        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/index.fxml"));

        applicationContext = new ClassPathXmlApplicationContext(contextPaths);
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        rootLoader.setControllerFactory(clazz -> {
            BatchController controller = null;
            BatchScheduledService scheduledService = new BatchScheduledService();
            JobParametersExtractor propertiesExtractor = applicationContext
                    .getBean("jobParametersFromPropertiesExtractor", JobParametersExtractor.class);

            scheduledService.setService(applicationContext
                    .getBean("BatchStartAndRecoveryService", BatchStartAndRecoveryService.class));

            if (clazz.isAssignableFrom(BatchIndexController.class)) {
                controller = new BatchIndexController();
            } else if (clazz.isAssignableFrom(MainBatchController.class)) {
                controller = new MainBatchController();
            } else if (clazz.isAssignableFrom(HandleOneHourDelayBatchController.class)) {
                controller = new HandleOneHourDelayBatchController();
            } else if (clazz.isAssignableFrom(HandleMaxMonthsBatchController.class)) {
                controller = new HandleMaxMonthsBatchController();
            }

            controller.setService(scheduledService);
            controller.setPropertiesExtractor(propertiesExtractor);

            return controller;
        });
        root = rootLoader.load();

        Platform.runLater(() -> scene = new Scene(root, 640, 480));

    }

    private void doStart(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        this.stage.setTitle("Raildelays");
        this.stage.setScene(scene);
        this.stage.show();

        notifyPreloader(new Preloader.StateChangeNotification(
                Preloader.StateChangeNotification.Type.BEFORE_START));
    }

    @Override
    public void stop() throws Exception {
        if (controller != null) {
            controller.destroy();
        }
        super.stop();
    }

    public static void main(String[] args) {
        // Simulate standalone mode
        LauncherImpl.launchApplication(Bootstrap.class, DataPreLoader.class, args);
    }
}
