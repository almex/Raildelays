package be.raildelays.javafx.controller.batch;

import be.raildelays.batch.service.BatchStartAndRecoveryService;
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
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.test.MetaDataInstanceFactory;

import static org.easymock.EasyMock.*;

/**
 * @author Almex
 */
@Category(GraphicalTest.class)
@RunWith(BlockJUnit4ClassRunner.class)
public class AbstractBatchControllerIT {

    private BatchControllerTest controller = new BatchControllerTest();

    @Rule
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    private BatchScheduledService service;
    private JobParametersExtractor extractor;
    private BatchStartAndRecoveryService recoveryService;

    @Before
    public void setUp() throws Exception {
        FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/test.fxml"));

        recoveryService = createMock(BatchStartAndRecoveryService.class);
        service = new BatchScheduledService();
        service.setService(recoveryService);
        extractor = createMock(JobParametersExtractor.class);
        controller.setJobName("foo");
        controller.setService(service);
        controller.setPropertiesExtractor(extractor);

        rootLoader.setControllerFactory(clazz -> controller);
        rootLoader.load();
    }

    @Test
    public void testDoAbandon() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();

        jobExecution.setStatus(BatchStatus.ABANDONED);

        expect(recoveryService.startNewInstance(anyString(), anyObject())).andReturn(jobExecution);
        expect(recoveryService.abandon(anyLong())).andReturn(jobExecution);
        replay(recoveryService);

        service.start("foo", new JobParameters());
        controller.doAbandon();
    }

    @Test
    public void testDoRestart() throws Exception {
        controller.doRestart();
    }

    @Test
    public void testDoStop() throws Exception {
        JobExecution jobExecution = MetaDataInstanceFactory.createJobExecution();

        jobExecution.setStatus(BatchStatus.STOPPING);

        expect(recoveryService.startNewInstance(anyString(), anyObject())).andReturn(jobExecution);
        expect(recoveryService.stop(anyLong())).andReturn(jobExecution);
        replay(recoveryService);

        service.start("foo", new JobParameters());
        controller.doStop();
    }

    @Test
    public void testDestroy() throws Exception {
        controller.destroy();
    }
}