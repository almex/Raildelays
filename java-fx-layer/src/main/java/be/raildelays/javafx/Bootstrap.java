package be.raildelays.javafx;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.controller.AbstractBatchController;
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
import java.net.URL;

/**
 * Bootstrap for JavaFX UI.
 *
 * @author Almex
 * @since 1.2
 */
public class Bootstrap extends Application {

    private AbstractBatchController controller;
    private FXMLLoader fxmlLoader;
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
        final String[] contextPaths = new String[]{"/spring/bootstrap-fx-context.xml"};
        URL location = getClass().getResource("/index.fxml");
        final BatchScheduledService scheduledService = new BatchScheduledService();

        fxmlLoader = new FXMLLoader(location);
        root = fxmlLoader.load();

        Platform.runLater(() -> scene = new Scene(root, 640, 480));

        applicationContext = new ClassPathXmlApplicationContext(contextPaths);
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        scheduledService.setPropertiesExtractor(applicationContext
                .getBean("jobParametersFromPropertiesExtractor", JobParametersExtractor.class));
        scheduledService.setService(applicationContext
                .getBean("BatchStartAndRecoveryService", BatchStartAndRecoveryService.class));

        controller = fxmlLoader.getController();
        controller.setService(scheduledService);
        controller.setJobName("mainJob");
        controller.initialize();
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
            controller.shutdownService();
        }
        super.stop();
    }

    public static void main(String[] args) {
        // Force the load of the CountBeanPostProcessor in the same ClassLoader as LauncherImpl
        Bootstrap.class.getClassLoader().getResource(CountBeanPostProcessor.class.getName());
        // Simulate standalone mode
        LauncherImpl.launchApplication(Bootstrap.class, DataPreLoader.class, args);
    }
}
