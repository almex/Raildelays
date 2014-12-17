package be.raildelays.batch.processor;

import be.raildelays.batch.bean.BatchExcelRow;
import be.raildelays.domain.Language;
import be.raildelays.domain.Sens;
import be.raildelays.domain.entities.Train;
import be.raildelays.test.RaildelaysTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;

import java.util.Date;
import java.util.Map;

public class StoreDelayGreaterThanThresholdInContextProcessorTest {

    private static String KEY_NAME = "key";
    private static Language language = Language.EN;
    /*
     * The System Under Test.
     */
    private StoreDelayGreaterThanThresholdInContextProcessor processor;
    private StepExecution stepbExecution;

    private BatchExcelRow input;

    @Before
    public void setUp() throws Exception {
        Train train = new Train("dummy");

        stepbExecution = MetaDataInstanceFactory.createStepExecution();

        processor = new StoreDelayGreaterThanThresholdInContextProcessor();
        processor.setKeyName(KEY_NAME);
        processor.setThreshold(60L);
        processor.beforeStep(stepbExecution);

        input = new BatchExcelRow.Builder(new Date(), Sens.DEPARTURE)
                .expectedTrain1(RaildelaysTestUtils.generateTrain("dummy", Language.EN))
                .build(false);
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

        Assert.assertEquals(input, ((Map) stepbExecution.getExecutionContext().get(KEY_NAME)).get(Sens.DEPARTURE));
    }
}