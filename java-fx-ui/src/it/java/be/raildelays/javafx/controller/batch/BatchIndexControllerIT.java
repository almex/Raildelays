package be.raildelays.javafx.controller.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
import be.raildelays.javafx.Bootstrap;
import be.raildelays.javafx.service.BatchScheduledService;
import be.raildelays.javafx.test.JavaFXThreadingRule;
import be.raildelays.test.GraphicalTest;
import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.batch.core.step.job.JobParametersExtractor;

import static org.easymock.EasyMock.createMock;

/**
 * @author Almex
 */
@Category(GraphicalTest.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class BatchIndexControllerIT {


    protected BatchIndexController controller;

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    protected BatchScheduledService service;
    protected JobParametersExtractor extractor;
    protected BatchStartAndRecoveryService recoveryService;
    protected FXMLLoader rootLoader;

    @Before
    public void setUp() throws Exception {
        recoveryService = createMock(BatchStartAndRecoveryService.class);
        service = new BatchScheduledService();
        service.setService(recoveryService);
        extractor = createMock(JobParametersExtractor.class);
        controller = new BatchIndexController();
        controller.setService(service);
        controller.setPropertiesExtractor(extractor);
    }

    /**
     * We expect that when loading all the Spring Context that the main Controller can be loaded without any error.
     * We wait 500ms then we destroy it.
     */
    @Test
    public void testInitialize() throws Exception {
        rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/index.fxml"));
        rootLoader.setControllerFactory(new Bootstrap.BatchControllerFactory(Bootstrap.initApplicationContext()));
        rootLoader.load();

        Thread.sleep(500);

        controller.destroy();
    }
}