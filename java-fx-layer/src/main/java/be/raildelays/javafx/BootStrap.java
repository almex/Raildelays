package be.raildelays.javafx;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.control.BatchControlPanel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Almex
 */
public class BootStrap extends Application {

    private MenuItem calculateItem;
    private Scene scene;
    private BatchControlPanel panel;

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap.class);


    @Override
    public void start(Stage primaryStage) {
        final Menu exitItem = new Menu("Exit");
        final Menu actionMenu = new Menu("Action");
        final MenuBar menuBar = new MenuBar();
        final BorderPane root = new BorderPane();
        final String[] contextPaths = new String[]{"/spring/bootstrap-fx-context.xml"};
        final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths);
        final BatchScheduledService scheduledService = new BatchScheduledService();

        //-- Initialize contexts
        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        final JobParametersExtractor propertiesExtractor = applicationContext
                .getBean("jobParametersFromPropertiesExtractor", JobParametersExtractor.class);
        final BatchStartAndRecoveryService batchStartAndRecoveryService = applicationContext
                .getBean("BatchStartAndRecoveryService", BatchStartAndRecoveryService.class);


        scheduledService.setPropertiesExtractor(propertiesExtractor);
        scheduledService.setService(batchStartAndRecoveryService);

        panel = new BatchControlPanel(scheduledService, "mainJob");
        calculateItem = new MenuItem("Compute");
        scene = new Scene(root, 640, 480);

        exitItem.setOnAction(event -> Platform.exit());

        actionMenu.getItems().add(calculateItem);
        menuBar.getMenus().setAll(exitItem, actionMenu);

        root.setTop(menuBar);
        root.setCenter(panel.getPane());
        primaryStage.setTitle("Test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        panel.shutdownService();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
