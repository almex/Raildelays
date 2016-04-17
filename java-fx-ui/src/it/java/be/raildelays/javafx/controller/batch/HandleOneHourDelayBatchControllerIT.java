package be.raildelays.javafx.controller.batch;

import javafx.fxml.FXMLLoader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;

import java.util.Collections;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Almex
 */
public class HandleOneHourDelayBatchControllerIT extends AbstractBatchControllerIT<HandleOneHourDelayBatchController> {

    @Before
    public void setUp() throws Exception {
        rootLoader = new FXMLLoader(getClass().getResource("/fxml/batch/handle-one-hour-delay-job.fxml"));
        controller = new HandleOneHourDelayBatchController();

        super.setUp();
    }

    /**
     * We expect only that a call to onBrowse() does not raise any error
     */
    @Test
    @Ignore // Cannot be tested otherwise it would open a file opener dialog box
    public void testOnBrowse() {
        JobParameters jobParameters = new JobParameters(
                Collections.singletonMap("excel.output.path", new JobParameter("./"))
        );

        expect(extractor.getJobParameters(null, null)).andReturn(jobParameters);
        replay(extractor);

        controller.onBrowse();
    }

}