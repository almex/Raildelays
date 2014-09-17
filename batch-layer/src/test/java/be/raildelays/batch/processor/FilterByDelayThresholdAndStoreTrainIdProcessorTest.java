package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Train;
import be.raildelays.test.RaildelaysTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;

public class FilterByDelayThresholdAndStoreTrainIdProcessorTest {

    private static String KEY_NAME = "key";
    private static Language language = Language.EN;
    /*
     * The System Under Test.
     */
    private FilterByDelayThresholdAndStoreTrainIdProcessor processor;
    private StepExecution stepbExecution;

    private BatchExcelRow input;

    @Before
    public void setUp() throws Exception {
        Train train = new Train("dummy");

        stepbExecution = MetaDataInstanceFactory.createStepExecution();

        processor = new FilterByDelayThresholdAndStoreTrainIdProcessor();
        processor.setKeyName(KEY_NAME);
        processor.setThreshold(60L);
        processor.beforeStep(stepbExecution);

        input = new BatchExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .build();
    }

    @Test
    public void testThatItemIsFiltered() throws Exception {
        input.setDelay(61L);

        Assert.assertNull(processor.process(input));
    }

    @Test
    public void testThatItemIsNotFiltered() throws Exception {
        input.setDelay(59L);

        Assert.assertNotNull(processor.process(input));
    }

    @Test
    public void testThatTrainIdIsInTheContext() throws Exception {
        input.setDelay(61L);
        processor.process(input);

        Assert.assertEquals(input.getExpectedTrain1().getId().longValue(), stepbExecution.getExecutionContext().getLong(KEY_NAME));
    }
}