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

package be.raildelays.javafx;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.controller.batch.*;
import be.raildelays.javafx.service.BatchScheduledService;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    private Scene scene;
    private Stage stage;
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
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/index.fxml"));
        applicationContext = initApplicationContext();

        rootLoader.setControllerFactory(new BatchControllerFactory(applicationContext));
        root = rootLoader.load();

        Platform.runLater(() -> scene = new Scene(root, 640, 480));
    }

    public static ClassPathXmlApplicationContext initApplicationContext() {
        final String[] contextPaths = new String[]{
                "/spring/bootstrap-fx-context.xml",
                "/jobs/main-job-context.xml",
                "/jobs/steps/handle-max-months-job-context.xml",
                "/jobs/steps/handle-more-than-one-hour-delays-job-context.xml",
                "/jobs/steps/load-gtfs-into-database-job-context.xml"
        };
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contextPaths);


        applicationContext.registerShutdownHook(); // Register close of this Spring context to shutdown of the JVM
        applicationContext.start();

        return applicationContext;
    }

    public static class BatchControllerFactory implements Callback<Class<?>, Object> {

        private ClassPathXmlApplicationContext applicationContext;

        public BatchControllerFactory(ClassPathXmlApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public Object call(Class<?> clazz) {
            BatchController controller = null;
            BatchScheduledService scheduledService = new BatchScheduledService();
            JobParametersExtractor propertiesExtractor = applicationContext.getBean(
                    "jobParametersFromPropertiesExtractor", JobParametersExtractor.class
            );

            scheduledService.setService(
                    applicationContext.getBean("batchStartAndRecoveryService", BatchStartAndRecoveryService.class)
            );

            if (clazz.isAssignableFrom(BatchIndexController.class)) {
                controller = new BatchIndexController();
            } else if (clazz.isAssignableFrom(MainBatchController.class)) {
                controller = new MainBatchController();
            } else if (clazz.isAssignableFrom(HandleOneHourDelayBatchController.class)) {
                controller = new HandleOneHourDelayBatchController();
            } else if (clazz.isAssignableFrom(HandleMaxMonthsBatchController.class)) {
                controller = new HandleMaxMonthsBatchController();
            } else if (clazz.isAssignableFrom(DownloadListOfTrainsBatchController.class)) {
                controller = new DownloadListOfTrainsBatchController();
            }

            if (controller != null) {
                controller.setService(scheduledService);
                controller.setPropertiesExtractor(propertiesExtractor);
            }

            LOGGER.info("The factory built a controller.");

            return controller;
        }
    }

    private void doStart(Stage primaryStage) throws IOException {
        this.stage = primaryStage; // We must keep reference to the Stage otherwise we get strage behaviour

        primaryStage.setTitle("Raildelays");
        primaryStage.setScene(scene);
        primaryStage.show();

        notifyPreloader(new Preloader.StateChangeNotification(
                Preloader.StateChangeNotification.Type.BEFORE_START));
    }

    @Override
    public void stop() throws Exception {
        if (controller != null) {
            controller.destroy();
        }
        if (applicationContext != null) {
            applicationContext.stop();
            applicationContext.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        // Simulate standalone mode
        LauncherImpl.launchApplication(Bootstrap.class, DataPreLoader.class, args);
    }
}
